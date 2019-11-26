package project;

import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.testfx.framework.junit.ApplicationTest;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.util.Duration;
import project.arena.ArenaInstance;
import project.controller.ArenaManager;
import project.controller.UIController;
import project.controller.UIController.GameMode;
import project.entity.*;
import project.entity.ArenaObjectFactory.MonsterType;
import project.entity.ArenaObjectFactory.TowerType;
import project.event.eventargs.ArenaObjectEventArgs;
import project.event.eventargs.EventArgs;

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
	 * Adds a tower to the currently active arena.
     * @param type The type of tower to create.
     * @param x The x-coordinate of the center of the tower.
     * @param y The y-coordinate of the center of the tower.
	 * @return The newly-created Tower object.
	 */
	protected final Tower addTowerToArena(TowerType type, short x, short y) {
		return ArenaObjectFactory.createTower(type, x, y);
	}

	/**
	 * Adds a tower to the currently active arena.
     * @param tower The tower from which this projectile originates.
     * @param target The monster that the projectile will pursue.
     * @param deltaX The x-offset from the targeted monster where the projectile will land.
     * @param deltaY The y-offset from the targeted monster where the projectile will land.
	 * @return The newly-created Projectile object.
	 */
	protected final Projectile addProjectileToArena(Tower tower, Monster target, short deltaX, short deltaY) {
		return ArenaObjectFactory.createProjectile(tower, target, deltaX, deltaY);
	}

	/**
	 * Adds a monster to the currently active arena.
     * @param type The type of monster to create.
     * @param x The x-coordinate of the monster.
     * @param y The x-coordinate of the monster.
     * @param difficulty The difficulty of the monster.
	 * @param isInvulnerable Whether the monster should be invulnerable.
	 * @return The newly-created Monster object.
	 */
	protected final Monster addMonsterToArena(MonsterType type, short x, short y, double difficulty, boolean isInvulnerable) {
		Monster m = ArenaObjectFactory.createMonster(type, x, y, difficulty);

		if (isInvulnerable) {
			try {
				Method method_setHealth = Monster.class.getDeclaredMethod("setHealth", double.class);
				method_setHealth.setAccessible(true);
				method_setHealth.invoke(m, Double.POSITIVE_INFINITY);
			} catch (Exception ex) {
				fail("An unexpected error has occurred");
			}
		}

		return m;
	}

	/**
	 * Removes an object from the currently active arena.
	 * @param o The object to remove.
	 */
	protected final void removeObjectFromArena(ArenaObject o) {
		ArenaManager.getActiveEventRegister().ARENA_OBJECT_REMOVE.invoke(this,
				new ArenaObjectEventArgs() {
					{ subject = o; }
				}
		);
	}

	/**
	 * Simulates the next frame of the game.
	 */
	protected final void simulateNextFrame() {
		interact(() -> {
			ArenaManager.getActiveEventRegister().ARENA_NEXT_FRAME.invoke(this, new EventArgs());
		});
	}

	/**
	 * Simulates a game but no additional monsters are spawned.
	 * Waits until the game has ended.
	 * @param speedMultiplier The rate at which to speed up the game.
	 */
	protected final void simulateGameNoSpawning(double speedMultiplier) {
		// Similar to Arena.run
		try {
			Field field_mode = UIController.class.getDeclaredField("mode");
			field_mode.setAccessible(true);
			if (field_mode.get(appController) == GameMode.END) {
				Method method_resetGame = UIController.class.getDeclaredMethod("resetGame");
				method_resetGame.setAccessible(true);
				method_resetGame.invoke(appController);
			}

			field_mode.set(appController, GameMode.SIMULATE);

			Method method_disableGameButton = UIController.class.getDeclaredMethod("disableGameButton");
			method_disableGameButton.setAccessible(true);
			method_disableGameButton.invoke(appController);

			Field field_timeline = UIController.class.getDeclaredField("timeline");
			field_timeline.setAccessible(true);
			Timeline timeline = (Timeline) field_timeline.get(appController);

			Method method_nextFrame = UIController.class.getDeclaredMethod("nextFrame");
			method_nextFrame.setAccessible(true);

			Field field_currentFrame = ArenaInstance.class.getDeclaredField("currentFrame");
			field_currentFrame.setAccessible(true);

			timeline = new Timeline(new KeyFrame(Duration.seconds(0.2 / speedMultiplier), e -> {
				try {
					field_currentFrame.set(arena, (int) field_currentFrame.get(arena) - 1); // Moves currentFrame back so monsters never spawn
					method_nextFrame.invoke(appController);
				} catch (Exception ex) {
					fail("An unexpected error has occurred");
				}
			}));
			timeline.setCycleCount(Timeline.INDEFINITE);
			timeline.play();
			field_timeline.set(appController, timeline);

			while (field_mode.get(arena) != GameMode.NORMAL) {}	// Wait until finished
		} catch (Exception e) {
			fail("An unexpected error has occurred");
		}
	}

	/**
	 * Simulate the action to build tower on specific grid.
	 * @param type The type of the tower.
	 * @param x The x-position of the grid.
	 * @param y The y-position of the grid.
	 */
	protected final void simulateBuildTowerGrid(TowerType type, short xPos, short yPos) {
		simulateBuildTower(type,
				(short) ((xPos + 0.5) * ArenaManager.GRID_WIDTH),
				(short) ((yPos + 0.5) * ArenaManager.GRID_HEIGHT)
		);
	}

	/**
	 * Simulate the action to build tower on specific coordinates.
	 * @param type The type of the tower.
	 * @param x The x-coordinate inside the paneArena.
	 * @param y The y-coordinate inside the paneArena.
	 */
	protected final void simulateBuildTower(TowerType type, short x, short y) {
		Label l = (Label)currentScene.lookup("#label" + type.name());
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
		short leftX = (short) (x / ArenaManager.GRID_WIDTH);
		short topY = (short) (y / ArenaManager.GRID_HEIGHT);

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