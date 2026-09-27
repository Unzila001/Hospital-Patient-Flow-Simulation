package view;

import simu.model.Customer;

public class PatientVisualisation {

    private Customer customer;

    private String patientLocation;

    public PatientVisualisation(Customer customer){
        this.customer = customer;
        patientLocation = "Arrival";
    }

    public int getPatientId(){
        return customer.getId();
    }

    public String getPatientLocation(){
        return patientLocation;
    }

    public void setPatientLocation(String location){
        patientLocation = location;
    }

}
