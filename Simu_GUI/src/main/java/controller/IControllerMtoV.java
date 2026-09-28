package controller;

import simu.model.Customer;

/* interface for the engine */
public interface IControllerMtoV {
    public void showEndTime(double time);

    public void visualiseCustomer(Customer customer);

    public void moveCustomer(int customerId, String location);

    public void updatePatientState(int customerId, String condition, boolean priority);

    public void updateWaitingStatus(int customerId, boolean waiting);

    public void removeCustomer(int customerId);

    public void showManagementAnalysis(String bottleneck,
                                       double averageWait,
                                       int maximumQueue,
                                       double utilization,
                                       int currentCapacity,
                                       int suggestedCapacity,
                                       String recommendation);
}
