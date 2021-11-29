## Cadence
Es compatible y configurable con Java, Spring Boot y Go.

Este **framework** tiene un paquete que te permite definir y crear arquitecturas basadas en eventos y sagas.

El back de Cadence utiliza por defecto como BBDD Cassandra o MySQL/Postgress. Se puede implementar un adaptador para poder utilizar cualquier otra BBDD, es decir, es totalmente configurable.

Estos son los pasos para inicializar el back de Cadence: 
### Download docker compose Cadence Server
> curl -O https://raw.githubusercontent.com/uber/cadence/master/docker/docker-compose.yml
> 
> docker-compose up

### Run cadence server host
> docker run --network=host --rm ubercadence/cli:master --do example domain register -rd 1

## Implementación del framework

El nucleo de **Cadence** esta basado en una unidad de estados llamada *workflow*, que es donde podemos implementar las Sagas. Un workflow esta compuesto por una serie de pasos y funciones que nos permite llevar a cabo una orquestación de un proceso entre sericios. 

El primer paso es crear la base de un workflow como una *interface* y definiendo los métodos que se van a utilizar.

Esto se hace con la anotación **@WorkflowMethod**: 
```
@WorkflowMethod
Long createOrder(Long customerId, Double totalMoney);
```

También tenemos **@SignalMethod**, que se utilizan para definir un método que va a reaccionar a una *signal*

```
@SignalMethod
void abandon();
```

Y por último tenemos los **@QueryMethod**, que se utilizan para indicar una funcion que va a reaccionar a una *query*

```
@QueryMethod(name="status")
String getStatus();
```

Luego de definir el **Workflow**, como lo hemos visto, se crea su implementación:

```
public class CreateOrderWorkflowImpl implements CreateOrderWorkflow {
```

Lo siguiente es definir las **Actividades**. Estás son funciones (async o sync) que son invocadas a lo largo de los pasos de un workflow.

Igual que el Workflow, las **Activities** tienen que definirse como una interface (en un módulo común), para luego implementarlas dentro del Workflow. 

Por ejemplo, cremaos la interfaz: 

```
public interface CustomerActivities {
	void reserveCredit(Long customerId, Double amount);
}
```

La implementación: 

```
public class OrderActivitiesImpl implements OrderActivities {
    @Override
    public Long createOrder (Long customerId, Double amount) {
        service.saveOrder(order);
        return order.getId();
    }
    .
    .
    .
}
```

Luego de tener la implementación se tiene que crear una instancia de esta actividad al inicializar el workflow utilizando un ActivityOptions Builder proporcionado por Cadence.

```
private final ActivityOptions orderActivityOptions = new ActivityOptions.Builder()
        .setTaskList("OrderTaskList")
        .setScheduleToCloseTimeout(Duration.ofSeconds(10))
        .build();
private final OrderActivities orderActivities =
            Workflow.newActivityStub(OrderActivities.class, orderActivityOptions);
```

Luego de tener los workflows y activities definidos e implementados, se construye el workflow y dentro de este se crea la respectiva saga. Para crear la saga se utiliza un Builder proporcionado por Cadence, de esta manera: 

```
Saga.Options sagaOptions = new Saga.Options.Builder().build();
Saga saga = new Saga(sagaOptions);
```

Luego de esto, se define el workflow ejecutando actividades que se añaden a la cola de la Saga, así como también compensaciones para esta Saga. De esta manera: 
```
Long orderId = orderActivities.createOrder(customerId, amount);
saga.addCompensation(orderActivities::rejectOrder, orderId, rejectedReason);
customerActivities.reserveCredit(customerId, amount);
```

Se puede hacer mediante un try y catch utilizando el método de saga.compensate(), de esta manera: 

```
} catch (ActivityFailureException e) {
    if(e.getCause() != null && e.getCause().getCause() instanceof CustomerNotFoundException) {
        rejectedReason = "CUSTOMER NOT FOUND";
    } else {
        rejectedReason = "CREDIT LIMIT EXCEEDED";
    }
    saga.compensate();
    throw e;
}
```

Al inicio de la app se define un @Bean donde se inicializa un workflowClient para que se pueda utilizar a lo largo de toda la app. Dicho esto: 

```
@Bean
WorkflowClient workflowClient() {
    IWorkflowService service = new WorkflowServiceTChannel(ClientOptions.defaultInstance());

    WorkflowClientOptions workflowClientOptions = WorkflowClientOptions.newBuilder()
            .setDomain("example")
            .build();
    return WorkflowClient.newInstance(service, workflowClientOptions);
}

@Bean
CommandLineRunner commandLineRunner(WorkflowClient workflowClient) {
    return args -> {
        WorkerFactory factory = WorkerFactory.newInstance(workflowClient);
        Worker worker = factory.newWorker("OrderTaskList");
        worker.registerWorkflowImplementationTypes(CreateOrderWorkflowImpl.class);
        worker.registerActivitiesImplementations(new OrderActivitiesImpl(service));
        factory.start();
    };
}
```

Esto nos permite utilizar el WorkflowClient a lo largo de la app para crear nuevas instancias o stubs de nuestro workflow, utilizando un Builder proporcionado por Cadence.

Con esto podemos crear metodos que inicialicen un workflow específico, por ejemplo:
```
public void save(Order order) {
    CreateOrderWorkflow workflow = workflowClient.newWorkflowStub(
            CreateOrderWorkflow.class,
            new WorkflowOptions.Builder()
                    .setExecutionStartToCloseTimeout(Duration.ofSeconds(10000))
                    .setTaskList("OrderTaskList")
                    .build()
    );
    WorkflowClient.execute(workflow::createOrder, order.getCustomerId(), order.getMoney());
}
```

Haciendo esto, corremos el workflow y por ende la saga para realizar el proceso de manera automatica según lo hayamos definido.



