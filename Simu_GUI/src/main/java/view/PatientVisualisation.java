package view;

import simu.model.Customer;

public class PatientVisualisation {

    private Customer customer;
    private String patientLocation;
    private boolean isWaiting = true;
    private boolean hasPriority = false;

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

    public void setWaiting(boolean status){
        isWaiting = status;
    }

    public boolean isWaiting() { return isWaiting; }

    public boolean getPriority() { return hasPriority; }

    public void changePriority(boolean priority){
        hasPriority = priority;
    }
}
