package view;

public interface IVisualisation {

	//Clear the hospital visualization when a new simulation starts
	void clearDisplay();

	//Show a newly arrived patient
	void newCustomer(int customerId);

	//Move an existing patient to another hospital service point
	void moveCustomer(int customerId, String location);

	//Update whether the patient is waiting or being served
	void updatePatientState(int customerId, String condition, boolean priority);

	//Remove the customer from visualization after discharge
	void removeCustomer(int customerId);
}

