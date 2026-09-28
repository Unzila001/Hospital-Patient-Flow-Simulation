package view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.geometry.VPos;
import javafx.scene.text.TextAlignment;
import javafx.scene.image.Image;
import simu.model.Customer;

import java.util.HashMap;
import java.util.Map;

public class Visualisation2 extends Canvas implements IVisualisation {
	private GraphicsContext gc;
	private Image patientImage;
	private Image priorityPatientImage;
	private int totalArrivals = 0;
	private int totalExits;

	//Store the current location of every patient
	private Map<Integer, PatientVisualisation> patients = new HashMap<>();
	private int latestCustomerId = -1;

	public Visualisation2(int w, int h) {
		super(w, h);
		gc = this.getGraphicsContext2D();

		patientImage = new Image(getClass().getResource("/images/patient1.png").toExternalForm());
		priorityPatientImage = new Image(getClass().getResource("/images/patientPriority.png").toExternalForm());

		clearDisplay();
	}

	@Override
	public void clearDisplay() {
		totalArrivals = 0;

		//Reset patient data for a new simulation
		patients.clear();
		latestCustomerId = -1;

		//Clear the complete canvas
		gc.setFill(Color.WHITE);
		gc.fillRect(0, 0, this.getWidth(), this.getHeight());

		//Hospital Title
		gc.setFill(Color.BLACK);
		gc.setFont(new Font(20));
		gc.fillText("Hospital Patient Flow Simulation", 20, 30);

		//Draw hospital service points
		drawServicePoint("Arrival", 20, 70, 100, 60);
		drawServicePoint("Registration", 140, 70, 130, 60);
		drawServicePoint("Triage", 290, 70, 130, 60);
		drawServicePoint("Doctor", 440, 70, 130, 60);

		drawServicePoint("Laboratory", 140, 180, 130, 60);
		drawServicePoint("X-ray", 290, 180, 130, 60);
		drawServicePoint("Treatment", 440, 180, 130, 60);
		drawServicePoint("Exit", 440, 290, 130, 60);

		//Draw the main patient flow arrows
		drawArrow(120, 100, 140, 100);		//Arrival -> Registration
		drawArrow(270, 100, 290, 100);		//Registration -> Triage
		drawArrow(420, 100, 440, 100);		//Triage -> Doctor

		drawDoctorRoutes();
		drawTreatmentRoutes();

		drawArrow(505, 240, 505, 290); 	//Treatment -> Exit
		drawDirectDischargeRoute();

		//Customer information panel
		gc.setFill(Color.web("#F7F9F7"));
		gc.fillRect(20, 370, 650, 105);

		//Panel border
		gc.setStroke(Color.web("#53635A"));
		gc.setLineWidth(1.5);
		gc.strokeRect(20, 370, 650, 105);

		//Panel title
		gc.setFill(Color.web("#53635A"));
		gc.setFont(new Font(16));
		gc.fillText("Customer Information", 35, 397);

		//Separator line
		gc.setStroke(Color.web("#C7D2CA"));
		gc.setLineWidth(1);
		gc.strokeLine(35, 407, 655, 407);

		//Initial information
		gc.setFill(Color.DARKGRAY);
		gc.setFont(new Font(14));
		gc.fillText("Waiting for patient arrival...", 35, 435);
	}

	//Draw one hospital service point
	private void drawServicePoint(String name, double x, double y, double width, double height) {

		//Soft hospital style background
		gc.setFill(Color.web("#E8EFEA"));
		gc.fillRect(x, y, width, height);

		//Box border
		gc.setStroke(Color.web("#53635A"));
		gc.setLineWidth(2);
		gc.strokeRect(x, y, width, height);

		//Service point text
		gc.setFill(Color.BLACK);
		gc.setFont(new Font(16));

		//Center text
		gc.setTextAlign(TextAlignment.CENTER);
		gc.setTextBaseline(VPos.CENTER);

		gc.fillText(name, x + width / 2, y + height / 2 );

		//Reset text setting form other text on canvas
		gc.setTextAlign(TextAlignment.LEFT);
		gc.setTextBaseline(VPos.BASELINE);
	}

	//Draw arrow between hospital service points
	private void drawArrow(double startX, double startY, double endX, double endY) {

		//Set arrow color and thickness
		gc.setStroke(Color.DARKGRAY);
		gc.setFill(Color.DARKGRAY);
		gc.setLineWidth(2);

		//Draw the main line
		gc.strokeLine(startX, startY, endX, endY);

		//Calculate the angle of the line
		double angle = Math.atan2(endY - startY, endX - startX);

		//Size of arrow head
		double arrowSize = 8;

		//Draw the 2 sides of arrow head
		gc.strokeLine(
				endX,
				endY,
				endX - arrowSize * Math.cos(angle - Math.PI / 6),
				endY - arrowSize * Math.sin(angle - Math.PI / 6)
		);

		gc.strokeLine(
				endX,
				endY,
				endX - arrowSize * Math.cos(angle + Math.PI / 6),
				endY - arrowSize * Math.sin(angle + Math.PI / 6)

		);
	}

	//Draw routes from Doctor to Lab, X-ray and Treatment
	private void drawDoctorRoutes() {

		gc.setStroke(Color.DARKGRAY);
		gc.setLineWidth(2);

		//Vertical lines down from doctor
		gc.strokeLine(505, 130, 505, 155);

		//Horizontal branching line
		gc.strokeLine(205, 155, 505, 155);

		//Branch to lab
		drawArrow(205, 155, 205, 180);

		//Branch to X-ray
		drawArrow(355, 155, 355, 180);

		//Branch to treatment
		drawArrow(505, 155, 505, 180);
	}

	//Draw routes from Lab and X-ray to treatment
	private void drawTreatmentRoutes() {

		gc.setStroke(Color.DARKGRAY);
		gc.setLineWidth(2);

		//Lab goes down to joining lines
		gc.strokeLine(205, 240, 205, 265);

		//X-ray goes down to joining line
		gc.strokeLine(355, 240, 355, 265);

		//Horizontal joining line towards Treatment
		gc.strokeLine(205, 265, 505, 265);

		//Arrow from joining line up to treatment
		drawArrow(505, 265, 505, 240);
	}

	//Draw direct discharge route from Doctor to Exit
	private void drawDirectDischargeRoute() {

		gc.setStroke(Color.DARKGRAY);
		gc.setLineWidth(2);

		//From doctor to right
		gc.strokeLine(570, 100, 620, 100);

		//Move down to the center level of exit
		gc.strokeLine(620, 100, 620, 320);

		//Arrow left into exit
		drawArrow(620, 320, 570, 320);
	}
	@Override
	public void newCustomer(Customer customer) {
		totalArrivals++;

		//Store new patient location
		PatientVisualisation patientVisualisation = new PatientVisualisation(customer);

		patients.put(
				patientVisualisation.getPatientId(),
				patientVisualisation
		);

		latestCustomerId = patientVisualisation.getPatientId();

		//Clear previous customer information
		gc.setFill(Color.web("#F7F9F7"));
		gc.fillRect(30, 412, 625, 55);

		//Arrival Label
		gc.setFill(Color.DARKGRAY);
		gc.setFont(new Font(14));
		gc.fillText("Latest arrival:", 35, 435);

		//Customer ID
		gc.setFill(Color.web("#53635A"));
		gc.setFont(new Font(16));
		gc.fillText("Customer #" + patientVisualisation.getPatientId(), 135, 435);

		//Current location label
		gc.setFill(Color.DARKGRAY);
		gc.setFont(new Font(14));
		gc.fillText("Location:", 330, 435);

		//Current location
		gc.setFill(Color.web("#53635A"));
		gc.setFont(new Font(16));
		gc.fillText("Arrival", 400, 435);

		//condition label
		gc.setFill(Color.DARKGRAY);
		gc.setFont(new Font(14));
		gc.fillText("Condition:", 35, 460);

		//Condition value
		gc.setFill(Color.web("#53635A"));
		gc.setFont(new Font(14));
		gc.fillText("Not assigned yet", 135, 460);

		//Priority label
		gc.setFill(Color.DARKGRAY);
		gc.setFont(new Font(14));
		gc.fillText("Priority:", 330, 460);

		//Priority value
		gc.setFill(Color.web("#53635A"));
		gc.setFont(new Font(14));
		gc.fillText("Not assigned yet", 400, 460);

		//Redraw all patients after customer information is updates
		refreshPatients();
	}

	@Override
	public void moveCustomer(int customerId, String location) {

		//Update patient's current location
		PatientVisualisation patient = patients.get(customerId);

		if(patient == null){
			return;
		}

		//Update patients current location
		patient.setPatientLocation(location);

		//Draw patient at the new location
		refreshPatients();

		//Update Customer Information only if this is the latest customer
		if (customerId == latestCustomerId) {

			//Clear old location value
			gc.setFill(Color.web("#F7F9F7"));
			gc.fillRect(395, 415, 250, 30);

			//Write new location
			gc.setFill(Color.web("#53635A"));
			gc.setFont(new Font(16));
			gc.fillText(location, 400, 435);
		}
	}

	@Override
	public void updatePatientState(int customerId, String condition, boolean priority) {
		// changing customer priority
		PatientVisualisation patient = patients.get(customerId);
		patient.changePriority(priority);

		//Only update Customer Information for the latest customer
		if (customerId == latestCustomerId) {

			//Clear the complete condition / priority row
			gc.setFill(Color.web("#F7F9F7"));
			gc.fillRect(30, 442, 625, 25);

			//Condition label
			gc.setFill(Color.DARKGRAY);
			gc.setFont(new Font(14));
			gc.fillText("Condition:", 35, 460);

			//Condition value
			gc.setFill(Color.web("#53635A"));
			gc.setFont((new Font(14)));
			gc.fillText(condition, 135, 460);

			//Priority label
			gc.setFill(Color.DARKGRAY);
			gc.setFont(new Font(14));
			gc.fillText("Priority:", 330, 460);

			//Priority value
			gc.setFill(Color.web("#53635A"));
			gc.setFont((new Font(14)));
			gc.fillText(priority ? "Yes" : "No", 400, 460);
		}
	}

	@Override
	public void removeCustomer(int customerId) {
		//Patient has completed the hospital flow
		totalExits++;

		//Remove patient from visualization
		patients.remove(customerId);

		//redraw remaining patients
		refreshPatients();

	}

	@Override
	public void updateWaitingStatus(int customerId, boolean waiting) {
		PatientVisualisation patient = patients.get(customerId);

		if(patient == null){
			return;
		}

		patient.setWaiting(waiting);
		refreshPatients();
	}

	//Count how many patients are currently at a location
	private int getPatientCount(String location) {
		int count = 0;
		for (PatientVisualisation patient : patients.values()) {
			if (location.equals(patient.getPatientLocation())) {
				count++;
			}
		}
		return count;
	}

	//Count priority patient at location
	private int getPriorityPatientCount(String location) {
		int count = 0;

		for (PatientVisualisation patient : patients.values()) {
			if (location.equals(patient.getPatientLocation()) && patient.getPriority()) {
				count++;
			}
		}
		return count;
	}

	//Draw one patient image with the number of patients at this location
	private void drawPatientCount(String location, double x, double y) {
		 int count;

		 //only draw if there is atleast one patient
		if (location.equals("Arrival")) {
			count = totalArrivals;
		} else if (location.equals("Exit")) {
			count = totalExits;
		} else {
			count = getPatientCount(location);
		}

		//only draw if there is atleast one patient
		if (count > 0) {
			//Total patients
			gc.drawImage(patientImage, x, y, 18, 32);

			//Draw one number beside the patient image
			gc.setFill(Color.BLACK);
			gc.setFont(new Font(14));
			gc.fillText(String.valueOf(count), x + 5, y - 3);
		}
	}

	private void drawPatient(int customerId, String location) {
		PatientVisualisation patient = patients.get(customerId);

		double x;
		double y;

		switch (location) {
			case "Arrival":
				x = 25;
				y = 85;
				break;

			case "Registration":
				x = 145;
				y = 85;
				break;

			case "Triage":
				x = 295;
				y = 85;
				break;

			case "Doctor":
				x =445;
				y = 85;
				break;

			case "Laboratory":
				x = 145;
				y = 195;
				break;

			case "X-ray":
				x = 295;
				y = 195;
				break;

			case "Treatment":
				x = 445;
				y = 195;
				break;

			case "Exit":
				x = 445;
				y = 305;
				break;

			default:
				return;
		}
		// Waiting patients are drawn semi transparent
		// Patients being served are drawn normally
		if(patient.isWaiting()){
			gc.setGlobalAlpha(0.2);
		} else {
			gc.setGlobalAlpha(1.0);
		}

		// Priority patients use different image
		if(patient.getPriority()) {
			gc.drawImage(priorityPatientImage, x, y, 18, 32);
		} else {
			gc.drawImage(patientImage, x, y, 18, 32);
		}

		// Returning transparency for rest of code
		gc.setGlobalAlpha(1.0);
	}

	//Refresh patient
	private void refreshPatients() {

		//Redraw all service-point boxes
		//This method will remove patient figures from their previous positions
		drawServicePoint("Arrival", 20, 70, 100, 60);
		drawServicePoint("Registration", 140, 70, 130, 60);
		drawServicePoint("Triage", 290, 70, 130, 60);
		drawServicePoint("Doctor", 440, 70, 130, 60);

		drawServicePoint("Laboratory", 140, 180, 130, 60);
		drawServicePoint("X-ray", 290, 180, 130, 60);
		drawServicePoint("Treatment", 440, 180, 130, 60);
		drawServicePoint("Exit", 440, 290, 130, 60);

		//Redraw all arrows and routes
		drawArrow(120, 100, 140, 100);		//Arrival -> Registration
		drawArrow(270, 100, 290, 100);		//Registration -> Triage
		drawArrow(420, 100, 440, 100);		//Triage -> Doctor

		drawDoctorRoutes();
		drawTreatmentRoutes();

		drawArrow(505, 240, 505, 290);		//Treatment -> Exit
		drawDirectDischargeRoute();

		//Draw one patient image and patient count at each location
		drawPatientCount("Arrival", 25, 85);
		drawPatientCount("Registration", 145, 85);
		drawPatientCount("Triage", 295, 85);
		drawPatientCount("Doctor", 445, 85);

		drawPatientCount("Laboratory", 145, 195);
		drawPatientCount("X-ray", 295, 195);
		drawPatientCount("Treatment", 445, 195);
		drawPatientCount("Exit", 445, 305);
	}
}
