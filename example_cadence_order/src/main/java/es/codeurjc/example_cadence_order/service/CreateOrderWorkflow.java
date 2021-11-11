package es.codeurjc.example_cadence_order.service;

import com.uber.cadence.workflow.WorkflowMethod;

public interface CreateOrderWorkflow {
	@WorkflowMethod
    Long createOrder(Long customerId, Double totalMoney);
}
