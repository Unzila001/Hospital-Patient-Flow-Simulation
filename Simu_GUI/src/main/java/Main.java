import dao.ServicePointResultDao;
import dao.SimulationRunDao;
import simu.model.ServicePointStats;
import view.SimulatorGUI;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/* to install JavaFX (adapted from https://openjfx.io/openjfx-docs/):
 *	- Open Module Settings
 * 		- Libraries, add
 *			- from Maven openjfx.javafx.fxml:19.0.2 (this includes all necessary other JavaFX libraries)
 *	- Run Configuration Edit
 *		- Modify Options/Add VM options
 *			- --module-path "lib" --add-modules javafx.controls,javafx.fxml
 */

public class Main { // Simulator using Java FX
	public static void main(String args[]) {
		SimulatorGUI.main(args);
	}
}
