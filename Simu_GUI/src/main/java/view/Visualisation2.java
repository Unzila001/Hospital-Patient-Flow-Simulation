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
		gc.setFill(Color.YELLOW);
		gc.fillRect(0, 0, this.getWidth(), this.getHeight());
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
