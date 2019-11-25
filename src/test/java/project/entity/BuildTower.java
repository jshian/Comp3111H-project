package project.entity;

import org.junit.Assert;
import org.junit.Test;

import project.JavaFXTester;
import project.arena.Coordinates;

public class BuildTower extends JavaFXTester {

    @Test
    public void testBuildTower() {
        Coordinates c = new Coordinates((short)50, (short)50);

        simulateBuildTower(TowerType.BasicTower, c);
        simulateBuildTower(TowerType.Catapult, c);
        Assert.assertTrue(hasTower(TowerType.BasicTower, c));
        Assert.assertTrue(!hasTower(TowerType.Catapult, c));
    }
}
