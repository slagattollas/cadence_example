package es.codeurjc.example_cadence_order.controller;

import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.example_cadence_order.domain.Order;
import es.codeurjc.example_cadence_order.service.OrderService;

@RestController
public class OrderController {

    @Autowired
	private OrderService service;
    
    @RequestMapping(value = "/orders", method = RequestMethod.GET)
    public Collection<Order> getAllOrders() {
        return service.findAll();
    }
       
    @RequestMapping(value = "/orders", method = RequestMethod.POST)
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        try {
            service.save(order);
        } catch(DataIntegrityViolationException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        
        return new ResponseEntity<>(order, HttpStatus.OK);
	}

    @RequestMapping(value="/orders/{orderId}", method= RequestMethod.GET)
    public ResponseEntity<Order> getOrder(@PathVariable long orderId) {
		Optional<Order> op = service.findById(orderId);
		if(op.isPresent()) {
			Order order = op.get();
			return new ResponseEntity<>(order, HttpStatus.OK);
		}else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

    @RequestMapping(value="/orders/{orderId}", method= RequestMethod.DELETE)
    public ResponseEntity<Order> deleteOrder(@PathVariable Long orderId) {
        Optional<Order> op = service.findById(orderId);
        if(op.isPresent()) {
            service.delete(orderId);
            Order order = op.get();
            return new ResponseEntity<>(order, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
}