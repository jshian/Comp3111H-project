package project;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.testfx.framework.junit.ApplicationTest;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import project.arena.Coordinates;
import project.arena.Grid;
import project.arena.towers.BuildTower;

/**
 * Base class to inherit from when testing objects that make use of JavaFX objects.
 */
public class JavaFXTester extends ApplicationTest {
	@Rule
	public final ExpectedException expectedException = ExpectedException.none();

	protected Scene s;

	public static enum TowerType { BasicTower, Catapult, IceTower, LaserTower }

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

	/**
	 * Simulate the action to build tower on specific coordinates.
	 * Recursively accesses each collection to also check the objects inside them.
	 * @param type The type of the tower.
	 * @param x The x-coordinate on paneArena.
	 * @param y The y-coordinate on paneArena.
	 */
	public void simulateBuildTower(BuildTower.TowerType type, short x, short y) {
		Label l = (Label)s.lookup("#label" + type.name());
		Bounds labelBound = l.localToScreen(l.getBoundsInLocal());
		AnchorPane b = (AnchorPane)s.lookup("#paneArena");
		Bounds paneBound = b.localToScreen(b.getBoundsInLocal());
		double sceneX = paneBound.getMinX();
		double sceneY = paneBound.getMinY();

		drag(labelBound.getMinX()+1, labelBound.getMinY()+1, MouseButton.PRIMARY);
		dropTo(sceneX+x, sceneY+y);
	}

	/**
	 * Test if there is a tower with specific tower type in the grid of specific coordinates.
	 * @param type The type of the tower.
	 * @param x The x-coordinate on paneArena.
	 * @param y The y-coordinate on paneArena.
	 */
	public boolean haveTower(BuildTower.TowerType type, short x, short y) {
		Coordinates topLeft = Grid.findGridTopLeft(new Coordinates(x,y));
		AnchorPane b = (AnchorPane)s.lookup("#paneArena");
		for (Node n : b.getChildren()) {
			if (n instanceof ImageView) {
				if (((ImageView) n).getX() == topLeft.getX() && ((ImageView) n).getX() == topLeft.getY()) {
					Image img = ((ImageView) n).getImage();
					Image compare = null;
					switch(type) {
						case BasicTower: compare = new Image("/basicTower.png", img.getWidth(), img.getHeight(), img.isPreserveRatio(), img.isSmooth()); break;
						case Catapult: compare = new Image("/catapult.png", img.getWidth(), img.getHeight(), img.isPreserveRatio(), img.isSmooth()); break;
						case IceTower: compare = new Image("/iceTower.png", img.getWidth(), img.getHeight(), img.isPreserveRatio(), img.isSmooth()); break;
						case LaserTower: compare = new Image("/laserTower.png", img.getWidth(), img.getHeight(), img.isPreserveRatio(), img.isSmooth()); break;
					}
					boolean equal = true;
					for (int i = 0; i < img.getHeight(); i++)
						for (int j = 0; j < img.getWidth(); j++)
							if (!img.getPixelReader().getColor(i, j).toString().equals(compare.getPixelReader().getColor(i, j).toString())) {
								equal = false;
								break;
							}
					if (equal == true)
						return true;
				}
			}
		}
		return false;
	}
}