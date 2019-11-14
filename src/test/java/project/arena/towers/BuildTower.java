package project.arena.towers;

import javafx.geometry.Bounds;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import org.junit.Test;
import project.JavaFXTester;

public class BuildTower extends JavaFXTester {

    public static enum TowerType { BasicTower, Catapult, IceTower, LaserTower }

    @Test
    public void testBuildTower() {
        AnchorPane b = (AnchorPane)s.lookup("#paneArena");

        simulateBuildTower(TowerType.BasicTower,50,50);
        simulateBuildTower(TowerType.Catapult,50,50);
    }

    /**
     * Simulate the action to build tower on specific coordinates.
     * Recursively accesses each collection to also check the objects inside them.
     * @param type The type of the tower.
     * @param x The x-coordinate on paneArena.
     * @param y The y-coordinate on paneArena.
     */
    public void simulateBuildTower(TowerType type, double x, double y) {
        AnchorPane b = (AnchorPane)s.lookup("#paneArena");
        Bounds paneBound = b.localToScreen(b.getBoundsInLocal());
        double sceneX = paneBound.getMinX();
        double sceneY = paneBound.getMinY();
        drag("#label" + type.name(), MouseButton.PRIMARY);
        dropTo(sceneX+x, sceneY+y);
    }
}
