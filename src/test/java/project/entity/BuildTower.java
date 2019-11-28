package project.entity;

import org.junit.Assert;
import org.junit.Test;

import project.JavaFXTester;
import project.entity.ArenaObjectFactory.TowerType;

public class BuildTower extends JavaFXTester {

    @Test
    public void testBuildTower() {
        short x = 50, y = 50;

        simulateBuildTower(TowerType.BASIC, x, y);
        simulateBuildTower(TowerType.CATAPULT, x, y);
        Assert.assertTrue(hasTower(TowerType.BASIC, x, y));
        Assert.assertTrue(!hasTower(TowerType.CATAPULT, x, y));
    }
}
