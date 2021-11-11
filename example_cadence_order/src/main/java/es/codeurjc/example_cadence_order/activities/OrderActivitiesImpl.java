package es.codeurjc.example_cadence_order.activities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.codeurjc.example_cadence_common.activities.OrderActivities;
import es.codeurjc.example_cadence_order.domain.Order;
import es.codeurjc.example_cadence_order.service.OrderService;

public class OrderActivitiesImpl implements OrderActivities{

    private static final Logger logger = LoggerFactory.getLogger(OrderActivitiesImpl.class);

	private OrderService service;

    public OrderActivitiesImpl(OrderService service) {
        this.service = service;
    }

    @Override
    public Long createOrder (Long customerId, Double amount) {
        System.out.println("Llega al create order");
        Order order = new Order(customerId, amount);
        //Order orderSaved = repository.saveAndFlush(order);
        System.out.println("Antes del repository");
        service.saveOrder(order);
        System.out.println("Order created in pending state");
        //logger.info("" + orderSaved.getId());
        System.out.println("Guardando orden con id: " + order.getId());
        //System.out.println("Devolviendo orden con id: " + orderSaved.getId());
        return order.getId();
    }

    @Override
    public void approveOrder(Long orderId) {
        logger.info("Order {} approved", orderId);
        service.update(orderId, "APPROVED", "");
    }
    
    @Override
    public void rejectOrder(Long orderId, String rejectionReason) {
        logger.info("Order {} rejected", orderId);
        service.update(orderId, "REJECTED", rejectionReason);
    }
}
