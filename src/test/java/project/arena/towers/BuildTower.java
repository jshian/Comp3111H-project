package project.arena.towers;

import org.junit.Assert;
import org.junit.Test;
import project.JavaFXTester;

public class BuildTower extends JavaFXTester {

    @Test
    public void testBuildTower() {
        simulateBuildTower(TowerType.BasicTower, (short)50, (short)50);
        simulateBuildTower(TowerType.Catapult, (short)50, (short)50);
        Assert.assertTrue(haveTower(TowerType.BasicTower, (short)50, (short)50));
        Assert.assertTrue(!haveTower(TowerType.Catapult, (short)50, (short)50));
    }


}
