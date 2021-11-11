package es.codeurjc.example_cadence_order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


import com.uber.cadence.client.WorkflowClient;
import com.uber.cadence.client.WorkflowClientOptions;
import com.uber.cadence.serviceclient.ClientOptions;
import com.uber.cadence.serviceclient.IWorkflowService;
import com.uber.cadence.serviceclient.WorkflowServiceTChannel;
import com.uber.cadence.worker.Worker;
import com.uber.cadence.worker.WorkerFactory;

import es.codeurjc.example_cadence_order.activities.OrderActivitiesImpl;
import es.codeurjc.example_cadence_order.service.CreateOrderWorkflowImpl;
import es.codeurjc.example_cadence_order.service.OrderService;

@SpringBootApplication
public class ExampleCadenceOrderApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExampleCadenceOrderApplication.class, args);
	}

    @Autowired
    OrderService service;

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
            /* AÑADIDO */
            worker.registerWorkflowImplementationTypes(CreateOrderWorkflowImpl.class);
            /* ------- */
            worker.registerActivitiesImplementations(new OrderActivitiesImpl(service));
            factory.start();
        };
    }

}
