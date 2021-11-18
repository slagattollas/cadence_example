package es.codeurjc.example_cadence_customer.activities;


import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.codeurjc.example_cadence_common.activities.CustomerActivities;
import es.codeurjc.example_cadence_common.exceptions.CreditLimitExceededException;
import es.codeurjc.example_cadence_common.exceptions.CustomerNotFoundException;
import es.codeurjc.example_cadence_customer.domain.Customer;
import es.codeurjc.example_cadence_customer.service.CustomerService;

public class CustomerActivitiesImpl implements CustomerActivities {

    private static final Logger logger = LoggerFactory.getLogger(CustomerActivitiesImpl.class);

    private CustomerService service;

    public CustomerActivitiesImpl(CustomerService service) {
        this.service = service;
    }

    @Override
    public void reserveCredit(Long customerId, Double money) {
        Optional<Customer> customer = service.findById(customerId);
        if(customer.isPresent()) {
            if (Double.compare(customer.get().getMoney(), money) > 0) {
                Customer optCustomer = customer.get();
                optCustomer.setMoney(optCustomer.getMoney() - money);
                service.save(optCustomer);
                logger.info("Credit re  served for customer {}", customerId);
            }else{
                logger.info("Credit limit is exceeded for customer {}", customerId);
                throw new CreditLimitExceededException();
            }
        } else {
            logger.info("Customer not found");
            throw new CustomerNotFoundException();
        }
    }
}
