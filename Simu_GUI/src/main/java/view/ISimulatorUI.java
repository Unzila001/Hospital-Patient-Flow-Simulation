package view;

public interface ISimulatorUI {
	// The Controller needs input which is passed to the Engine
	public double getTime();
	public long getDelay();
	public long getSeed();
	
	// Controller gives Engine produced results to the UI
	public void setEndingTime(double time);
	
	// Controller requires
	public IVisualisation getVisualisation();

	//Management Analysis
	 public void showManagementAnalysis(String bottleneck,
										double averageWait,
										int maximumQueue,
										double utilization,
										int currentCapacity,
										int suggestedCapacity,
										String recommendation);
}
