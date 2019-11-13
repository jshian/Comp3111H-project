package project;

import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.testfx.framework.junit.ApplicationTest;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Base class to inherit from when testing objects that make use of JavaFX objects.
 */
public class JavaFXTester extends ApplicationTest {
	@Rule
	public final ExpectedException expectedException = ExpectedException.none();

	protected Scene s;

	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/project.fxml"));
		Parent root = loader.load();
		primaryStage.setTitle("Tower Defence");
		s = new Scene(root, 600, 480);
		primaryStage.setScene(s);
		primaryStage.show();
		UIController appController = (UIController)loader.getController();
		appController.createArena();
	}
}