package es.codeurjc.example_cadence_customer.domain;

import javax.persistence.*;
import java.util.Collections;
import java.util.Map;

@Entity
public class Customer {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long customerId = null;
	
	private String name;
	
	private Double money;

	public Customer() {}

	public Customer(String name, Double money) {
		this.name = name;
		this.money = money;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getMoney() {
		return money;
	}

	public void setMoney(Double money) {
		this.money = money;
	}

	public Long getId() {
		return customerId;
	}

	public void setId(long id) {
		this.customerId = id;
	}

}

