package project;

import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import org.junit.Rule;
import org.junit.rules.ExpectedException;

import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import project.arena.ArenaInstance;
import project.control.ArenaManager;
import project.entity.ArenaObjectFactory;
import project.entity.Tower;
import project.entity.ArenaObjectFactory.TowerType;
import project.event.EventHandler;
import project.event.eventargs.EventArgs;
import project.ui.UIController;
import project.ui.UIController.GameMode;

/**
 * Base class to inherit from when testing objects that make use of JavaFX objects.
 */
public class JavaFXTester extends ApplicationTest {
	@Rule
	public final ExpectedException expectedException = ExpectedException.none();

	protected Stage primaryStage;
	protected Scene currentScene;
	protected UIController appController;

	// For convenience
	protected static final short ZERO = (short) 0;

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
	 * Simulate the action to build tower on specific grid.
	 * @param type The type of the tower.
	 * @param x The x-position of the grid.
	 * @param y The y-position of the grid.
	 */
	protected final void addTowerToGrid(TowerType type, short xPos, short yPos) {
		Tower t = ArenaObjectFactory.createTower(this, type, ArenaManager.getGridCenterXFromPos(xPos), ArenaManager.getGridCenterYFromPos(yPos));
		appController.setTowerEvent(t);
	}

	/**
	 * Simulates the next frame of the game.
	 */
	protected final void simulateNextFrameNoSpawning() {
		try {
			Field field_currentFrame = ArenaInstance.class.getDeclaredField("currentFrame");
			field_currentFrame.setAccessible(true);
			
			ArenaInstance arena = ArenaManager.getActiveArenaInstance();
			field_currentFrame.set(arena, (int) field_currentFrame.get(arena) - 1);

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

		ArenaManager.getActiveEventRegister().ARENA_NEXT_FRAME.subscribe(onNextFrame);

		try {
			Field field_mode = UIController.class.getDeclaredField("mode");
			field_mode.setAccessible(true);
			
			Method method_simulate = UIController.class.getDeclaredMethod("simulate");
			method_simulate.setAccessible(true);
			method_simulate.invoke(appController);

			WaitForAsyncUtils.waitFor(60, TimeUnit.SECONDS, () -> ((GameMode) field_mode.get(ArenaManager.getActiveEventRegister()) == GameMode.END));
			WaitForAsyncUtils.waitForFxEvents();
			clickOn("OK");
		} catch (Exception e) {
			fail("An unexpected error has occurred");
		}

		ArenaManager.getActiveEventRegister().ARENA_NEXT_FRAME.unsubscribe(onNextFrame);
	}
}