package view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class Visualisation2 extends Canvas implements IVisualisation {
	private GraphicsContext gc;

	public Visualisation2(int w, int h) {
		super(w, h);
		gc = this.getGraphicsContext2D();
		clearDisplay();
	}

	@Override
	public void clearDisplay() {
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
	}

	//Draw one hospital service point
	private void drawServicePoint(String name, double x, double y, double width, double height) {

		//Box background
		gc.setFill(Color.LIGHTBLUE);
		gc.fillRect(x, y, width, height);

		//Box border
		gc.setStroke(Color.BLACK);
		gc.setLineWidth(2);
		gc.strokeRect(x, y, width, height);

		//Service point names
		gc.setFill(Color.BLACK);
		gc.setFont(new Font(16));
		gc.fillText(name, x + 10, y + 25);
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
	public void newCustomer(int customerId) {
		gc.setFill(Color.RED);
		gc.setFont(new Font(20));
		gc.fillText("Customer " + customerId, 100, 100);
	}

	@Override
	public void moveCustomer(int customerId, String location) {
		//Patient movement will be implemented later
	}

	@Override
	public void updatePatientState(int customerId, String state) {
		//Patient state visualization will be implemented later
	}

	@Override
	public void removeCustomer(int customerId) {
		//Patient removal will be implemented later
	}
}
