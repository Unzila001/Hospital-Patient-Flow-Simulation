package controller;

import javafx.application.Platform;
import simu.framework.IEngine;
import simu.model.Customer;
import simu.model.MyEngine;
import view.ISimulatorUI;

public class Controller implements IControllerVtoM, IControllerMtoV {   // NEW
    private IEngine engine;
    private ISimulatorUI ui;

    public Controller(ISimulatorUI ui) {
        this.ui = ui;
    }

    /* Engine control: */
    @Override
    public void startSimulation() {
        engine = new MyEngine(this, ui.getSeed());
        engine.setSimulationTime(ui.getTime());
        engine.setDelay(ui.getDelay());

        ui.getVisualisation().clearDisplay();
        ((Thread) engine).start();
        //((Thread)engine).run(); // Never like this, why?
    }

    @Override
    public void decreaseSpeed() { // hidastetaan moottorisäiettä
        engine.setDelay((long) (engine.getDelay() * 1.10));
    }

    @Override
    public void increaseSpeed() { // nopeutetaan moottorisäiettä
        engine.setDelay((long) (engine.getDelay() * 0.9));
    }


    /* Simulation results passing to the UI
     * Because FX-UI updates come from engine thread, they need to be directed to the JavaFX thread
     */
    @Override
    public void showEndTime(double time) {
        Platform.runLater(() -> ui.setEndingTime(time));
    }

    @Override
    public void visualiseCustomer(Customer customer) {
        Platform.runLater(() -> ui.getVisualisation().newCustomer(customer));
    }


    @Override
    public void moveCustomer(int customerId, String location) {
        Platform.runLater(() ->
                ui.getVisualisation().moveCustomer(customerId, location));
    }

    @Override
    public void updatePatientState(int customerId, String condition, boolean priority) {
        Platform.runLater(() ->
                ui.getVisualisation().updatePatientState(
                        customerId,
                        condition,
                        priority));
    }

    @Override
    public void updateWaitingStatus(int customerId, boolean waiting) {
        Platform.runLater(() ->
                ui.getVisualisation().updateWaitingStatus(customerId, waiting)
        );
    }

    @Override
    public void removeCustomer(int customerId) {
        Platform.runLater(() ->
                ui.getVisualisation().removeCustomer(customerId)
        );
    }

    @Override
    public void showManagementAnalysis(String bottleneck,
                                       double averageWait,
                                       int maximumQueue,
                                       double utilization,
                                       int currentCapacity,
                                       int suggestedCapacity,
                                       String recommendation) {
        Platform.runLater(() ->
                ui.showManagementAnalysis(
                        bottleneck,
                        averageWait,
                        maximumQueue,
                        utilization,
                        currentCapacity,
                        suggestedCapacity,
                        recommendation
                ));

    }
}
