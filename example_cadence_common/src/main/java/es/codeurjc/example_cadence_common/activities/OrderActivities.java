package es.codeurjc.example_cadence_common.activities;

public interface OrderActivities {
	Long createOrder(Long customerId, Double money);
    void approveOrder(Long orderId);
    void rejectOrder(Long orderId, String rejectionReason);
}
