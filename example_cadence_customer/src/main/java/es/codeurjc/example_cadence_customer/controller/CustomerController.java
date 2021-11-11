package es.codeurjc.example_cadence_customer.controller;

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

import es.codeurjc.example_cadence_customer.domain.Customer;
import es.codeurjc.example_cadence_customer.service.CustomerService;

@RestController
public class CustomerController {
    
    @Autowired
	private CustomerService service;
    
    @RequestMapping(value = "/customers", method = RequestMethod.GET)
    public Collection<Customer> getAllCustomers() {
        return service.findAll();
    }
       
    @RequestMapping(value = "/customers", method = RequestMethod.POST)
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
        try {
            service.save(customer);
        } catch(DataIntegrityViolationException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        
        return new ResponseEntity<>(customer, HttpStatus.OK);
	}

    @RequestMapping(value="/customers/{customerId}", method= RequestMethod.GET)
    public ResponseEntity<Customer> getCustomer(@PathVariable long customerId) {
		Optional<Customer> op = service.findById(customerId);
		if(op.isPresent()) {
			Customer customer = op.get();
			return new ResponseEntity<>(customer, HttpStatus.OK);
		}else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

    @RequestMapping(value="/customers/{customerId}", method= RequestMethod.DELETE)
    public ResponseEntity<Customer> deleteCustomer(@PathVariable Long customerId) {
        Optional<Customer> op = service.findById(customerId);
        if(op.isPresent()) {
            service.delete(customerId);
            Customer customer = op.get();
            return new ResponseEntity<>(customer, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
}
