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
import project.UIController.GameMode;
import project.arena.Arena;
import project.arena.ArenaObjectFactory.MonsterType;
import project.arena.Coordinates;
import project.arena.Grid;
import project.arena.monsters.Fox;
import project.arena.monsters.Monster;
import project.arena.monsters.Penguin;
import project.arena.monsters.Unicorn;
import project.arena.towers.BuildTower;

/**
 * Base class to inherit from when testing objects that make use of JavaFX objects.
 */
public class JavaFXTester extends ApplicationTest {
	@Rule
	public final ExpectedException expectedException = ExpectedException.none();

	protected Stage primaryStage;
	protected Scene currentScene;
	protected UIController appController;

	public static enum TowerType { BasicTower, Catapult, IceTower, LaserTower }

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
	 * Accesses the arena that is currently loaded.
	 * @return The arena that is currently loaded.
	 */
	protected final Arena getCurrentArena() {
		Arena arena = null;

		try {
			Field f = UIController.class.getDeclaredField("arena");
			f.setAccessible(true);
			arena = (Arena) f.get(this.appController);
		} catch (Exception e) {
			fail("An unexpected error has occurred");
		}

		return arena;
	}

	/**
	 * Adds a monster to the arena that is currently loaded.
	 * @param monsterType The type of the monster.
     * @param start The starting location of the monster.
     * @param destination The destination of the monster. It will try to move there.
     * @param difficulty The difficulty of the monster, which should be at least equal to <code>1</code>.
	 * @param isInvulnerable Whether the monster should be invulnerable.
	 */
	protected final void addMonsterToArena(MonsterType monsterType, Coordinates start, Coordinates end, double difficulty, boolean isInvulnerable) {
		final Arena arena = getCurrentArena();
		ImageView iv = null;
		Monster m = null;
		switch (monsterType) {
			case Fox:
				iv = new ImageView(new Image("/fox.png", UIController.GRID_WIDTH / 4, UIController.GRID_HEIGHT / 4, true, true));
				m = new Fox(arena, start, end, iv, difficulty);
				break;
			case Penguin:
				iv = new ImageView(new Image("/penguin.png", UIController.GRID_WIDTH / 4, UIController.GRID_HEIGHT / 4, true, true));
				m = new Penguin(arena, start, end, iv, difficulty);
				break;
			case Unicorn: default:
				iv = new ImageView(new Image("/unicorn.png", UIController.GRID_WIDTH / 4, UIController.GRID_HEIGHT / 4, true, true));
				m = new Unicorn(arena, start, end, iv, difficulty);
				break;
		}

		if (isInvulnerable) {
			try {
				Method method_setHealth = Monster.class.getDeclaredMethod("setHealth", double.class);
				method_setHealth.setAccessible(true);
				method_setHealth.invoke(m, Double.POSITIVE_INFINITY);
			} catch (Exception ex) {
				fail("An unexpected error has occurred");
			}
		}

		final Monster finalMonster = m;
		
		interact(new Runnable() {
            @Override
            public void run() {
				getCurrentArena().addObject(finalMonster);
            }
        });
	}

	/**
	 * Simulates a game but no additional monsters are spawned.
	 * Waits until the game has ended.
	 * @param speedMultiplier The rate at which to speed up the game.
	 */
	protected final void simulateGameNoSpawning(double speedMultiplier) {
		final Arena arena = getCurrentArena();

		// Similar to Arena.run
		try {
			Field field_mode = UIController.class.getDeclaredField("mode");
			field_mode.setAccessible(true);
			if (field_mode.get(arena) == GameMode.end) {
				Method method_resetGame = UIController.class.getDeclaredMethod("resetGame");
				method_resetGame.setAccessible(true);
				method_resetGame.invoke(appController);
			}

			field_mode.set(arena, GameMode.simulate);

			Method method_disableGameButton = UIController.class.getDeclaredMethod("disableGameButton");
			method_disableGameButton.setAccessible(true);
			method_disableGameButton.invoke(appController);

			Field field_timeline = UIController.class.getDeclaredField("timeline");
			field_timeline.setAccessible(true);
			Timeline timeline = (Timeline) field_timeline.get(appController);

			Method method_nextFrame = UIController.class.getDeclaredMethod("nextFrame");
			method_nextFrame.setAccessible(true);

			Field field_currentFrame = Arena.class.getDeclaredField("currentFrame");
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

			while (field_mode.get(arena) != GameMode.normal) {}	// Wait until finished
		} catch (Exception e) {
			fail("An unexpected error has occurred");
		}
	}

	/**
	 * Simulate the action to build tower on specific {@link Grid}.
	 * @param type The type of the tower.
	 * @param xPos The x-position of the grid.
	 * @param yPos The y-position of the grid.
	 */
	protected final void simulateBuildTower(BuildTower.TowerType type, short xPos, short yPos) {
		simulateBuildTower(type, Grid.findGridCenter(xPos, yPos));
	}

	/**
	 * Simulate the action to build tower on specific coordinates.
	 * @param type The type of the tower.
	 * @param coordinates The coordinates inside the paneArena.
	 */
	protected final void simulateBuildTower(TowerType type, Coordinates coordinates) {
		Label l = (Label)currentScene.lookup("#label" + type.name());
		Bounds labelBound = l.localToScreen(l.getLayoutBounds());
		AnchorPane b = (AnchorPane)currentScene.lookup("#paneArena");
		Bounds paneBound = b.localToScreen(b.getLayoutBounds());
		double sceneX = paneBound.getMinX();
		double sceneY = paneBound.getMinY();

		drag(labelBound.getMinX()+1, labelBound.getMinY()+1, MouseButton.PRIMARY);
		dropTo(sceneX+coordinates.getX(), sceneY+coordinates.getY());
	}

	/**
	 * Test if there is a tower with specific tower type in specific {@link Grid}.
	 * @param type The type of the tower.
	 * @param xPos The x-position of the grid.
	 * @param yPos The y-position of the grid.
	 */
	protected final void hasTower(BuildTower.TowerType type, short xPos, short yPos) {
		simulateBuildTower(type, Grid.findGridCenter(xPos, yPos));
	}

	/**
	 * Test if there is a tower with specific tower type in the grid containing the specified coordinates.
	 * @param type The type of the tower.
	 * @param coordinates The coordinates inside the paneArena.
	 */
	protected final boolean hasTower(TowerType type, Coordinates coordinates) {
		Coordinates topLeft = Grid.findGridTopLeft(coordinates);
		AnchorPane b = (AnchorPane)currentScene.lookup("#paneArena");
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