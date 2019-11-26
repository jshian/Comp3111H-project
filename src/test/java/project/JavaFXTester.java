package project;

import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.Rule;
import org.junit.rules.ExpectedException;

import org.testfx.framework.junit.ApplicationTest;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import project.arena.ArenaInstance;
import project.control.ArenaManager;
import project.entity.ArenaObject;
import project.entity.ArenaObjectFactory.TowerType;
import project.event.EventHandler;
import project.event.eventargs.ArenaObjectEventArgs;
import project.event.eventargs.EventArgs;
import project.ui.UIController;

/**
 * Base class to inherit from when testing objects that make use of JavaFX objects.
 */
public class JavaFXTester extends ApplicationTest {
	@Rule
	public final ExpectedException expectedException = ExpectedException.none();

	protected Stage primaryStage;
	protected Scene currentScene;
	protected UIController appController;

	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/project.fxml"));
		Parent root = loader.load();
		this.primaryStage.setTitle("Tower Defence");
		this.currentScene = new Scene(root, 600, 480);
		this.primaryStage.setScene(this.currentScene);
		this.primaryStage.show();
		this.appController = (UIController)loader.getController();
		this.appController.createArena();
	}

	/**
	 * Simulates the next frame of the game.
	 */
	protected final void simulateNextFrame() {
		try {
			Method method_nextFrame = UIController.class.getDeclaredMethod("nextFrame");
			method_nextFrame.setAccessible(true);
			method_nextFrame.invoke(appController);
		} catch (Exception e) {
			fail("An unexpected error has occurred");
		}
	}

	/**
	 * Simulates a game but no additional monsters are spawned.
	 * Waits until the game has ended.
	 * @param speedMultiplier The rate at which to speed up the game.
	 */
	protected final void simulateGameNoSpawning(double speedMultiplier) {
		EventHandler<EventArgs> onNextFrame = (sender, args) -> {
			try {
				Field field_currentFrame = ArenaInstance.class.getDeclaredField("currentFrame");
				field_currentFrame.setAccessible(true);
	
				ArenaInstance arena = ArenaManager.getActiveArenaInstance();
				field_currentFrame.set(arena, (int) field_currentFrame.get(arena) - 1);
			} catch (Exception e) {
				fail("An unexpected error has occurred");
			}
		};
		
		EventHandler<EventArgs> onGameover = (sender, args) -> {
			notify();
		};

		ArenaManager.getActiveEventRegister().ARENA_NEXT_FRAME.subscribe(onNextFrame);
		ArenaManager.getActiveEventRegister().ARENA_GAME_OVER.subscribe(onGameover);

		try {
			Field field_mode = UIController.class.getDeclaredField("mode");
			field_mode.setAccessible(true);
			
			Method method_play = UIController.class.getDeclaredMethod("play");
			method_play.setAccessible(true);
			method_play.invoke(appController);

			wait();
		} catch (Exception e) {
			fail("An unexpected error has occurred");
		}

		ArenaManager.getActiveEventRegister().ARENA_NEXT_FRAME.unsubscribe(onNextFrame);
		ArenaManager.getActiveEventRegister().ARENA_GAME_OVER.unsubscribe(onGameover);
	}

	/**
	 * Simulate the action to build tower on specific grid.
	 * @param type The type of the tower.
	 * @param x The x-position of the grid.
	 * @param y The y-position of the grid.
	 */
	protected final void simulateBuildTowerGrid(TowerType type, short xPos, short yPos) {
		simulateBuildTower(type, ArenaManager.getGridCenterXFromPos(xPos), ArenaManager.getGridCenterYFromPos(yPos));
	}

	/**
	 * Simulate the action to build tower on specific coordinates.
	 * @param type The type of the tower.
	 * @param x The x-coordinate inside the paneArena.
	 * @param y The y-coordinate inside the paneArena.
	 */
	protected final void simulateBuildTower(TowerType type, short x, short y) {
		Label l = (Label)currentScene.lookup("#label" + type.getDefaultName());
		Bounds labelBound = l.localToScreen(l.getLayoutBounds());
		AnchorPane b = (AnchorPane)currentScene.lookup("#paneArena");
		Bounds paneBound = b.localToScreen(b.getLayoutBounds());
		double sceneX = paneBound.getMinX();
		double sceneY = paneBound.getMinY();

		drag(labelBound.getMinX()+1, labelBound.getMinY()+1, MouseButton.PRIMARY);
		dropTo(sceneX+x, sceneY+y);
	}

	/**
	 * Test if there is a tower with specific tower type in the grid containing the specified coordinates.
	 * @param type The type of the tower.
	 * @param x The x-coordinate inside the paneArena.
	 * @param y The y-coordinate inside the paneArena.
	 */
	protected final boolean hasTower(TowerType type, short x, short y) {
		short leftX = ArenaManager.getGridLeftXFromCoor(x);
		short topY = ArenaManager.getGridTopYFromCoor(y);

		AnchorPane b = (AnchorPane)currentScene.lookup("#paneArena");
		for (Node n : b.getChildren()) {
			if (n instanceof ImageView) {
				if (((ImageView) n).getX() == leftX && ((ImageView) n).getY() == topY) {
					Image img = ((ImageView) n).getImage();
					Image compare = null;
					switch(type) {
						case BASIC: compare = new Image("/basicTower.png", img.getWidth(), img.getHeight(), img.isPreserveRatio(), img.isSmooth()); break;
						case CATAPULT: compare = new Image("/catapult.png", img.getWidth(), img.getHeight(), img.isPreserveRatio(), img.isSmooth()); break;
						case ICE: compare = new Image("/iceTower.png", img.getWidth(), img.getHeight(), img.isPreserveRatio(), img.isSmooth()); break;
						case LASER: compare = new Image("/laserTower.png", img.getWidth(), img.getHeight(), img.isPreserveRatio(), img.isSmooth()); break;
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