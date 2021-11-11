package es.codeurjc.example_cadence_order.domain;

import javax.persistence.*;

@Entity
@Table(name = "order_table")
public class Order {
    
    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id = null;
    
    //@Enumerated(EnumType.STRING)
    private String state;

    private Long customerId;
    
    private Double money;

    //@Enumerated(EnumType.STRING)
    private String rejectionReason;

    public Order() {
        this.state = "PENDING";
    }

    public Order(Long customerId, Double money) {
        this.state = "PENDING";
        this.customerId = customerId;
        this.money = money;
        this.rejectionReason = "";
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state){
        this.state = state;
    }

    public Long getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(Long id) {
        this.customerId = id;
    }

    public Double getMoney() {
        return this.money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }

    public String getRejectionReason() {
        return this.rejectionReason;    
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}

