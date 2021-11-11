package es.codeurjc.example_cadence_order.service;

import java.time.Duration;

import com.uber.cadence.activity.ActivityOptions;
import com.uber.cadence.workflow.ActivityFailureException;
import com.uber.cadence.workflow.Saga;
import com.uber.cadence.workflow.Workflow;

import es.codeurjc.example_cadence_common.activities.CustomerActivities;
import es.codeurjc.example_cadence_common.activities.OrderActivities;

public class CreateOrderWorkflowImpl implements CreateOrderWorkflow {

    private final ActivityOptions customerActivityOptions = new ActivityOptions.Builder()
            .setTaskList("CustomerTaskList")
            .setScheduleToCloseTimeout(Duration.ofSeconds(10))
            .build();
    private final CustomerActivities customerActivities =
            Workflow.newActivityStub(CustomerActivities.class, customerActivityOptions);

    private final ActivityOptions orderActivityOptions = new ActivityOptions.Builder()
            .setTaskList("OrderTaskList")
            .setScheduleToCloseTimeout(Duration.ofSeconds(10))
            .build();
    private final OrderActivities orderActivities =
            Workflow.newActivityStub(OrderActivities.class, orderActivityOptions);

    @Override
    public Long createOrder(Long customerId, Double amount) {
        System.out.print("Entrando al principio de la funcion");
        Saga.Options sagaOptions = new Saga.Options.Builder().build();
        Saga saga = new Saga(sagaOptions);
        System.out.print("Entro aca");
        String rejectedReason = "";
        try {
            Long orderId = orderActivities.createOrder(customerId, amount);
            System.out.print("Entro aca");
            saga.addCompensation(orderActivities::rejectOrder, orderId, rejectedReason);
            customerActivities.reserveCredit(customerId, amount);
            orderActivities.approveOrder(orderId);
            System.out.print("Entro aca");
            return orderId;
        } catch (ActivityFailureException e) {
            System.out.println(e.getMessage());
            saga.compensate();
            throw e;
        }
    }
}
