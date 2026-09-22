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
		drawServicePoint("Registration", 30, 70, 130, 60);
		drawServicePoint("Triage", 200, 70, 130, 60);
		drawServicePoint("Doctor", 370, 70, 130, 60);

		drawServicePoint("Laboratory", 200, 180, 130, 60);
		drawServicePoint("X-ray", 370, 180, 130, 60);
		drawServicePoint("Treatment", 540, 180, 130, 60);
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
