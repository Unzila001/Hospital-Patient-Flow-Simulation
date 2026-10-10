package simu.model;

/**
 * Represents the statistics collected for a service point.
 */
public class ServicePointStats {

    private String serviceName;
    private double averageWaitingTime;
    private int maxQueueLength;
    private int currentCapacity;
    private double totalServiceTime;
    private int numberStarted;
    private double utilization;


    /**
     * Optional empty constructor for the class.
     */
    public ServicePointStats(){}

    /**
     * Creates a ServicePointStats object with the specified statistics.
     *
     * @param serviceName the services name
     * @param averageWaitingTime the waiting time for the service
     * @param maxQueueLength the queue length for the service
     * @param currentCapacity the current capacity for the service
     * @param totalServiceTime total service time
     * @param numberStarted how many services were started
     * @param utilization the utilization of the service
     */
    public ServicePointStats(String serviceName, double averageWaitingTime, int maxQueueLength, int currentCapacity, double totalServiceTime, int numberStarted, double utilization){
        this.serviceName = serviceName;
        this.averageWaitingTime = averageWaitingTime;
        this.maxQueueLength = maxQueueLength;
        this.currentCapacity = currentCapacity;
        this.totalServiceTime = totalServiceTime;
        this.numberStarted = numberStarted;
        this.utilization = utilization;
    }

    /**
     * Gets the service point name.
     *
     * @return the name of the service point
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * Sets the service point name.
     *
     * @param serviceName the given service point name
     */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * Gets the average waiting time of the service point.
     *
     * @return the average waiting time
     */
    public double getAverageWaitingTime() {
        return averageWaitingTime;
    }

    /**
     * Sets the average waiting time of the service point.
     *
     * @param averageWaitingTime the given average waiting time
     */
    public void setAverageWaitingTime(double averageWaitingTime) {
        this.averageWaitingTime = averageWaitingTime;
    }

    /**
     * Gets the max queue length of the service point.
     *
     * @return the max queue length
     */
    public int getMaxQueueLength() {
        return maxQueueLength;
    }

    /**
     * Sets the max queue length of the service point.
     *
     * @param maxQueueLength the given max queue length
     */
    public void setMaxQueueLength(int maxQueueLength) {
        this.maxQueueLength = maxQueueLength;
    }

    /**
     * Gets the current capacity of the service point.
     *
     * @return the current capacity
     */
    public int getCurrentCapacity() {
        return currentCapacity;
    }

    /**
     * Sets the current capacity of the service point.
     *
     * @param currentCapacity the given current capacity
     */
    public void setCurrentCapacity(int currentCapacity) {
        this.currentCapacity = currentCapacity;
    }

    /**
     * Gets the total service time of the service point.
     *
     * @return the total service time
     */
    public double getTotalServiceTime() {
        return totalServiceTime;
    }

    /**
     * Sets the total service time of the service point.
     *
     * @param totalServiceTime the given total sevice time
     */
    public void setTotalServiceTime(double totalServiceTime) {
        this.totalServiceTime = totalServiceTime;
    }

    /**
     * Gets the amount of started services of this service point.
     *
     * @return the number of started services
     */
    public int getNumberStarted() {
        return numberStarted;
    }

    /**
     * Sets the number of started services of this service point.
     *
     * @param numberStarted the given amount of started services
     */
    public void setNumberStarted(int numberStarted) {
        this.numberStarted = numberStarted;
    }

    /**
     * Gets the utilization of the service point.
     *
     * @return the utilization
     */
    public double getUtilization() {
        return utilization;
    }

    /**
     * Sets the utilization of the service point.
     *
     * @param utilization the given utilization
     */
    public void setUtilization(double utilization) {
        this.utilization = utilization;
    }

}
