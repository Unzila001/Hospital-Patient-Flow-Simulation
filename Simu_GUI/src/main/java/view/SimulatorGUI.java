package view;

import java.text.DecimalFormat;
import controller.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import simu.framework.Trace;
import simu.framework.Trace.Level;
import javafx.scene.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;

import java.text.DecimalFormat;

public class SimulatorGUI extends Application implements ISimulatorUI {

	// Controller object (UI needs)
	private IControllerVtoM controller;

	// UI Components:
	private TextField time;
	private TextField delay;
	private Label results;
	private Label timeLabel;
	private Label delayLabel;
	private Label resultLabel;

	private Label patientsServedValue;
	private Label waitingTimeValue;
	private Label hospitalTimeValue;
	private Label queueInfoValue;
	private Label throughputValue;
	private Label doctorUtilizationValue;

	private Button startButton;
	private Button slowButton;
	private Button speedUpButton;

	private IVisualisation display;


	@Override
	public void init() {
		Trace.setTraceLevel(Level.INFO);
		controller = new Controller(this);
	}

	@Override
	public void start(Stage primaryStage) {
		// UI creation
		try {
			primaryStage.setOnCloseRequest(t -> {
                Platform.exit();
                System.exit(0);
            });

			primaryStage.setTitle("Simulator");

			startButton = new Button();
			startButton.setText("Start simulation");
			startButton.setOnAction(event -> {
                controller.startSimulation();
                startButton.setDisable(true);
            });

			slowButton = new Button();
			slowButton.setText("Slow down");
			slowButton.setOnAction(e -> controller.decreaseSpeed());

			speedUpButton = new Button();
			speedUpButton.setText("Speed up");
			speedUpButton.setOnAction(e -> controller.increaseSpeed());

			timeLabel = new Label("Simulation time:");
			timeLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
	        time = new TextField("Give time");
	        time.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
	        time.setPrefWidth(150);

	        delayLabel = new Label("Delay:");
			delayLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
	        delay = new TextField("Give delay");
	        delay.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
	        delay.setPrefWidth(150);
	                	        
	        resultLabel = new Label("Total time:");
			resultLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
	        results = new Label();
	        results.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
	        results.setPrefWidth(150);

	        HBox hBox = new HBox();
	        hBox.setPadding(new Insets(15, 12, 15, 12)); // margins up, right, bottom, left
	        hBox.setSpacing(10);   // node distance 10 pixel
	        
	        GridPane grid = new GridPane();
	        grid.setAlignment(Pos.CENTER);
	        grid.setVgap(10);
	        grid.setHgap(5);

	        grid.add(timeLabel, 0, 0);   // column, row
	        grid.add(time, 1, 0);
	        grid.add(delayLabel, 0, 1);
	        grid.add(delay, 1, 1);
	        grid.add(resultLabel, 0, 2);
	        grid.add(results, 1, 2);
	        grid.add(startButton,0, 3);
	        grid.add(speedUpButton, 0, 4);
	        grid.add(slowButton, 1, 4);

			Label statisticsTitle = new Label("Results / Statistics");
			statisticsTitle.setFont(Font.font("Tahoma", FontWeight.BOLD, 20));

			patientsServedValue = new Label("-");
			waitingTimeValue = new Label("-");
			hospitalTimeValue = new Label("-");
			queueInfoValue = new Label("-");
			throughputValue = new Label("-");
			doctorUtilizationValue = new Label("-");

			GridPane statisticsGrid = new GridPane();
			statisticsGrid.setHgap(15);
			statisticsGrid.setVgap(10);
			statisticsGrid.setPadding(new Insets(15));

			statisticsGrid.add(statisticsTitle, 0, 0, 2, 1);
			statisticsGrid.add(new Label("Patients served:"), 0, 1);
			statisticsGrid.add(patientsServedValue, 1, 1);

			statisticsGrid.add(new Label("Average waiting time:"), 0, 2);
			statisticsGrid.add(waitingTimeValue, 1, 2);

			statisticsGrid.add(new Label("Average hospital time:"), 0, 3);
			statisticsGrid.add(hospitalTimeValue, 1, 3);

			statisticsGrid.add(new Label("Queue information:"), 0, 4);
			statisticsGrid.add(queueInfoValue, 1, 4);

			statisticsGrid.add(new Label("Throughput:"), 0, 5);
			statisticsGrid.add(throughputValue, 1, 5);

			statisticsGrid.add(new Label("Doctor utilization:"), 0, 6);
			statisticsGrid.add(doctorUtilizationValue, 1, 6);
	        
	        display = new Visualisation2(700,400);
			//display = new Visualisation2(400,200);

	        // Fill the box:
			hBox.getChildren().addAll(grid, (Canvas) display, statisticsGrid);
	        Scene scene = new Scene(hBox);
	        primaryStage.setScene(scene);
	        primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/* UI interface methods (controller calls) */
	@Override
	public double getTime(){
		return Double.parseDouble(time.getText());
	}

	@Override
	public long getDelay(){
		return Long.parseLong(delay.getText());
	}

	@Override
	public void setEndingTime(double time) {
		 DecimalFormat formatter = new DecimalFormat("#0.00");
		 this.results.setText(formatter.format(time));
	}

	public void setPatientsServed(int patients) {
		patientsServedValue.setText(String.valueOf(patients));
	}

	public void setWaitingTime(double waitingTime) {
		waitingTimeValue.setText(String.format("%.2f min", waitingTime));
	}

	public void setHospitalTime(double hospitalTime) {
		hospitalTimeValue.setText(String.format("%.2f min", hospitalTime));
	}

	public void setQueueInfo(String queueInfo) {
		queueInfoValue.setText(queueInfo);
	}

	public void setThroughput(double throughput) {
		throughputValue.setText(String.format("%.2f patients/hour", throughput));
	}

	public void setDoctorUtilization(double utilization) {
		doctorUtilizationValue.setText(String.format("%.2f %%", utilization));
	}

	@Override
	public IVisualisation getVisualisation() {
		 return display;
	}

	/* JavaFX-application (UI) start-up */
	public static void main(String[] args) {
		launch(args);
	}
}
