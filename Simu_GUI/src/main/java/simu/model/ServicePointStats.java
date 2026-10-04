package simu.model;

public class ServicePointStats {

    private String serviceName;
    private double averageWaitingTime;
    private int maxQueueLength;
    private int currentCapacity;
    private double totalServiceTime;
    private int numberStarted;
    private double utilization;

    public ServicePointStats(){}

    public ServicePointStats(String serviceName, double averageWaitingTime, int maxQueueLength, int currentCapacity, double totalServiceTime, int numberStarted, double utilization){
        this.serviceName = serviceName;
        this.averageWaitingTime = averageWaitingTime;
        this.maxQueueLength = maxQueueLength;
        this.currentCapacity = currentCapacity;
        this.totalServiceTime = totalServiceTime;
        this.numberStarted = numberStarted;
        this.utilization = utilization;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public double getAverageWaitingTime() {
        return averageWaitingTime;
    }

    public void setAverageWaitingTime(double averageWaitingTime) {
        this.averageWaitingTime = averageWaitingTime;
    }

    public int getMaxQueueLength() {
        return maxQueueLength;
    }

    public void setMaxQueueLength(int maxQueueLength) {
        this.maxQueueLength = maxQueueLength;
    }

    public int getCurrentCapacity() {
        return currentCapacity;
    }

    public void setCurrentCapacity(int currentCapacity) {
        this.currentCapacity = currentCapacity;
    }

    public double getTotalServiceTime() {
        return totalServiceTime;
    }

    public void setTotalServiceTime(double totalServiceTime) {
        this.totalServiceTime = totalServiceTime;
    }

    public int getNumberStarted() {
        return numberStarted;
    }

    public void setNumberStarted(int numberStarted) {
        this.numberStarted = numberStarted;
    }

    public double getUtilization() {
        return utilization;
    }

    public void setUtilization(double utilization) {
        this.utilization = utilization;
    }

}
