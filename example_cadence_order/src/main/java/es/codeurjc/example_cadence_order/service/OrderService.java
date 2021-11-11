package es.codeurjc.example_cadence_order.service;

import java.time.Duration;
import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uber.cadence.client.WorkflowClient;
import com.uber.cadence.client.WorkflowOptions;

import es.codeurjc.example_cadence_order.domain.Order;
import es.codeurjc.example_cadence_order.domain.OrderRepository;

@Service
public class OrderService {
    
	@Autowired
	private WorkflowClient workflowClient;

    @Autowired
	private OrderRepository repository;

	public Collection<Order> findAll() {
		return repository.findAll();
	}

	public Optional<Order> findById(Long id) {
		return repository.findById(id);
	}

	public void saveOrder(Order order) {
		repository.save(order);
	}

	public void save(Order order) {
		CreateOrderWorkflow workflow = workflowClient.newWorkflowStub(
                CreateOrderWorkflow.class,
                new WorkflowOptions.Builder()
                        .setExecutionStartToCloseTimeout(Duration.ofSeconds(10000))
                        .setTaskList("OrderTaskList")
                        .build()
        );
		WorkflowClient.execute(workflow::createOrder, order.getCustomerId(), order.getMoney());
		//repository.findById(id);
		System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
		System.out.println(order.getId());
		System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
	}

	public void delete(Long id) {
		repository.deleteById(id);
	}
    
	public void update(Long id, String state, String rejectReason) {
		Optional<Order> optional = repository.findById(id);
		if(optional.isPresent()) {
			Order order = optional.get();
			order.setState(state);
			order.setRejectionReason(rejectReason);
			repository.save(order);
		}
	}
}
