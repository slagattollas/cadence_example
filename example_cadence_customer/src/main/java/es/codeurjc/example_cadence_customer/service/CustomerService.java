package es.codeurjc.example_cadence_customer.service;

import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.example_cadence_customer.domain.Customer;
import es.codeurjc.example_cadence_customer.domain.CustomerRepository;

@Service
public class CustomerService {

    @Autowired
	private CustomerRepository repository;

	public Collection<Customer> findAll() {
		return repository.findAll();
	}

	public Optional<Customer> findById(Long id) {
		return repository.findById(id);
	}

	public void save(Customer customer) {
		repository.save(customer);
	}

	public void delete(Long id) {
		repository.deleteById(id);
	}
    
}
