package es.codeurjc.example_cadence_customer;

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

import es.codeurjc.example_cadence_customer.activities.CustomerActivitiesImpl;
import es.codeurjc.example_cadence_customer.service.CustomerService;

@SpringBootApplication
public class ExampleCadenceCustomerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExampleCadenceCustomerApplication.class, args);
	}

    @Autowired
    CustomerService service;

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
            Worker worker = factory.newWorker("CustomerTaskList");
            worker.registerActivitiesImplementations(new CustomerActivitiesImpl(service));
            factory.start();
        };
    }
}
