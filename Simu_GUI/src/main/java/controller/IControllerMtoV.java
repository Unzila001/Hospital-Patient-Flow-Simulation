package controller;

import simu.model.Customer;

/* interface for the engine */
public interface IControllerMtoV {
		public void showEndTime(double time);
		public void visualiseCustomer(Customer customer);
		public void moveCustomer(int customerId, String location);
		public void updatePatientState(int customerId, String condition, boolean priority);
}
