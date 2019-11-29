package project.entity;

import org.junit.Assert;
import org.junit.Test;
import project.JavaFXTester;

public class TowerTest extends JavaFXTester {
    @Test
    public void rangeTest() {
        BasicTower b1 = new BasicTower((short)200,(short)200);
        Assert.assertTrue(b1.isInRange((short)205,(short)200));
        Assert.assertTrue(b1.isInRange((short)200,(short)205));
        Assert.assertTrue(b1.isInRange((short)140,(short)200));
        Assert.assertTrue(b1.isInRange((short)260,(short)200));
        Assert.assertTrue(b1.isInRange((short)200,(short)260));
        Assert.assertTrue(b1.isInRange((short)200,(short)140));
        Assert.assertTrue(b1.isInRange((short)170,(short)170));
        Assert.assertTrue(b1.isInRange((short)230,(short)230));
        Assert.assertTrue(b1.isInRange((short)170,(short)230));
        Assert.assertTrue(b1.isInRange((short)230,(short)170));
        Assert.assertTrue(!b1.isInRange((short)400,(short)20));
        Assert.assertTrue(!b1.isInRange((short)20,(short)400));
        Assert.assertTrue(!b1.isInRange((short)270,(short)200));
        Assert.assertTrue(!b1.isInRange((short)200,(short)270));

        IceTower i1 = new IceTower((short)200,(short)200);
        Assert.assertTrue(i1.isInRange((short)205,(short)200));
        Assert.assertTrue(i1.isInRange((short)200,(short)205));
        Assert.assertTrue(i1.isInRange((short)160,(short)200));
        Assert.assertTrue(i1.isInRange((short)240,(short)200));
        Assert.assertTrue(i1.isInRange((short)200,(short)240));
        Assert.assertTrue(i1.isInRange((short)200,(short)160));
        Assert.assertTrue(i1.isInRange((short)180,(short)180));
        Assert.assertTrue(i1.isInRange((short)220,(short)220));
        Assert.assertTrue(i1.isInRange((short)180,(short)220));
        Assert.assertTrue(i1.isInRange((short)220,(short)180));
        Assert.assertTrue(!i1.isInRange((short)400,(short)20));
        Assert.assertTrue(!i1.isInRange((short)20,(short)400));
        Assert.assertTrue(!i1.isInRange((short)251,(short)200));
        Assert.assertTrue(!i1.isInRange((short)200,(short)251));

        Catapult c1 = new Catapult((short)200, (short)200);
        Assert.assertTrue(c1.isInRange((short)140,(short)200));
        Assert.assertTrue(c1.isInRange((short)260,(short)200));
        Assert.assertTrue(c1.isInRange((short)160,(short)160));
        Assert.assertTrue(c1.isInRange((short)240,(short)240));
        Assert.assertTrue(c1.isInRange((short)150,(short)250));
        Assert.assertTrue(c1.isInRange((short)250,(short)150));
        Assert.assertTrue(!c1.isInRange((short)200,(short)200));
        Assert.assertTrue(!c1.isInRange((short)180,(short)220));
        Assert.assertTrue(!c1.isInRange((short)350,(short)201));
        Assert.assertTrue(!c1.isInRange((short)330,(short)330));

        LaserTower l1 = new LaserTower((short)200, (short)200);
        Assert.assertTrue(l1.isInRange((short)290,(short)200));
        Assert.assertTrue(l1.isInRange((short)200,(short)110));
        Assert.assertTrue(l1.isInRange((short)210,(short)210));
        Assert.assertTrue(l1.isInRange((short)250,(short)250));
        Assert.assertTrue(!l1.isInRange((short)310,(short)200));
        Assert.assertTrue(!l1.isInRange((short)400,(short)400));
        Assert.assertTrue(!l1.isInRange((short)280,(short)130));
        Assert.assertTrue(!l1.isInRange((short)0,(short)0));
    }

    @Test
    public void upgradeTest() {
        BasicTower b1 = new BasicTower((short)200,(short)200);
        b1.upgrade(); Assert.assertTrue(b1.getAttackPower()==15);
        Assert.assertTrue(b1.getBuildValue()==20);
        b1.upgrade(); Assert.assertTrue(b1.getAttackPower()==20);
        Assert.assertTrue(b1.getBuildValue()==30);
        for (int i = 0; i <= 20; i++) {
            b1.upgrade(); Assert.assertTrue(b1.getAttackPower()<=100);
            Assert.assertTrue(b1.getBuildValue()==40+i*10);
        }

        IceTower i1 = new IceTower((short)200,(short)200);
        i1.upgrade(); Assert.assertTrue(i1.getSlowDownTime()==15);
        Assert.assertTrue(i1.getBuildValue()==25);
        i1.upgrade(); Assert.assertTrue(i1.getSlowDownTime()==20);
        Assert.assertTrue(i1.getBuildValue()==35);
        for (int i = 0; i <= 20; i++) {
            i1.upgrade(); Assert.assertTrue(i1.getSlowDownTime()<=100);
            Assert.assertTrue(i1.getBuildValue()==45+i*10);
        }

        Catapult c1 = new Catapult((short)200, (short)200);
        c1.upgrade(); Assert.assertTrue(c1.getReload()==19);
        Assert.assertTrue(c1.getBuildValue()==40);
        c1.upgrade(); Assert.assertTrue(c1.getReload()==18);
        Assert.assertTrue(c1.getBuildValue()==60);
        for (int i = 0; i <= 20; i++) {
            c1.upgrade(); Assert.assertTrue(c1.getReload()>=2);
            Assert.assertTrue(c1.getBuildValue()==80+i*20);
        }

        LaserTower l1 = new LaserTower((short)200, (short)200);
        l1.upgrade(); Assert.assertTrue(l1.getAttackPower()==35);
        Assert.assertTrue(l1.getBuildValue()==30);
        l1.upgrade(); Assert.assertTrue(l1.getAttackPower()==40);
        Assert.assertTrue(l1.getBuildValue()==40);
        for (int i = 0; i <= 20; i++) {
            l1.upgrade(); Assert.assertTrue(l1.getAttackPower()<=100);
            Assert.assertTrue(l1.getBuildValue()==50+i*10);
        }

    }

}
