package testing;

import project.*;
import project.monsters.*;
import project.towers.*;

import org.junit.*;
import org.testfx.framework.junit.*;

import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.fxml.*;

public class JUnitTester extends ApplicationTest {

	private Scene s;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample.fxml"));
		Parent root = loader.load();
		primaryStage.setTitle("Tower Defence");
		s = new Scene(root, 600, 480);
		primaryStage.setScene(s);
		primaryStage.show();
		MyController appController = (MyController)loader.getController();
		appController.createArena();   		
	}

	
	@Test
	public void testNextFrameButton() {
		clickOn("#buttonNextFrame");
		AnchorPane b = (AnchorPane)s.lookup("#paneArena");
		for (javafx.scene.Node i : b.getChildren()) {
			if (i.getClass().getName().equals("javafx.scene.control.Label")) {
				Label h = (Label)i;
				if (h.getLayoutX() == 0 && h.getLayoutY() == 0)
					Assert.assertEquals(h.getText(), "M");
			}
		}
	}
} 