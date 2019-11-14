package project.arena.towers;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import org.junit.Assert;
import org.junit.Test;
import project.JavaFXTester;
import project.arena.Coordinates;
import project.arena.Grid;

public class BuildTower extends JavaFXTester {

    public static enum TowerType { BasicTower, Catapult, IceTower, LaserTower }

    @Test
    public void testBuildTower() {
        AnchorPane b = (AnchorPane)s.lookup("#paneArena");

        simulateBuildTower(TowerType.BasicTower, (short)50, (short)50);
        simulateBuildTower(TowerType.Catapult, (short)50, (short)50);
        Assert.assertTrue(haveTower(TowerType.BasicTower, (short)50, (short)50));
        Assert.assertTrue(!haveTower(TowerType.Catapult, (short)50, (short)50));
    }

    /**
     * Simulate the action to build tower on specific coordinates.
     * Recursively accesses each collection to also check the objects inside them.
     * @param type The type of the tower.
     * @param x The x-coordinate on paneArena.
     * @param y The y-coordinate on paneArena.
     */
    public void simulateBuildTower(TowerType type, short x, short y) {
        AnchorPane b = (AnchorPane)s.lookup("#paneArena");
        Bounds paneBound = b.localToScreen(b.getBoundsInLocal());
        double sceneX = paneBound.getMinX();
        double sceneY = paneBound.getMinY();
        // this work
        // drag("#paneArena", MouseButton.PRIMARY);
        // this need mouse to move little bit so that dropTo runs. Plz help.
        drag("#label" + type.name(), MouseButton.PRIMARY);
        dropTo(sceneX+x, sceneY+y);
    }

    /**
     * Test if there is a tower with specific tower type in the grid of specific coordinates.
     * @param type The type of the tower.
     * @param x The x-coordinate on paneArena.
     * @param y The y-coordinate on paneArena.
     */
    public boolean haveTower(TowerType type, short x, short y) {
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
