package project.entity;

import java.util.PriorityQueue;

import org.junit.Assert;
import org.junit.Test;

import project.Geometry;
import project.JavaFXTester;
import project.entity.Monster;
import project.entity.ArenaObjectFactory.MonsterType;
import project.entity.ArenaObjectFactory.TowerType;

/**
 * Tests the {@link Catapult} class.
 */
public class CatapultTest extends JavaFXTester {

    @Test
    public void selectMonster() {
        PriorityQueue<Monster> testList = new PriorityQueue<>();

        //test shooting range valid
        Catapult catapult = (Catapult) ArenaObjectFactory.createTower(TowerType.CATAPULT, (short) 370, (short) 10);
        Monster m1 = ArenaObjectFactory.createMonster(MonsterType.UNICORN, (short) 110, (short) 110, 1); testList.add(m1);
        Monster m2 = ArenaObjectFactory.createMonster(MonsterType.UNICORN, (short) 130, (short) 110, 1); testList.add(m2);
        Monster m3 = ArenaObjectFactory.createMonster(MonsterType.UNICORN, (short) 450, (short) 10, 1); testList.add(m3);
        for (Monster m : testList) {
            // So they can't move
            m.baseSpeed = 0;
            m.speed = 0;
        }

        simulateNextFrame();
        Assert.assertTrue(catapult.monstersInSplashRange.size() == 1 && catapult.monstersInSplashRange.peek() == m3);
        for (Monster m :catapult.monstersInSplashRange) {
            Assert.assertTrue(Geometry.isInCircle( m.getX(), m.getY(), catapult.targetLocationX, catapult.targetLocationY,25));
        }

        //test shoot monster with nearest but not most monsters
        Monster m4 = ArenaObjectFactory.createMonster(MonsterType.UNICORN, (short) 330, (short) 10, 1); testList.add(m4);
        Monster m5 = ArenaObjectFactory.createMonster(MonsterType.UNICORN, (short) 330, (short) 10, 1); testList.add(m5);
        for (Monster m : testList) {
            // So they can't move
            m.baseSpeed = 0;
            m.speed = 0;
        }

        simulateNextFrame();
        Assert.assertTrue(catapult.monstersInSplashRange.size() == 1 && catapult.monstersInSplashRange.peek() == m3);
        for (Monster m :catapult.monstersInSplashRange) {
            Assert.assertTrue(Geometry.isInCircle( m.getX(), m.getY(), catapult.targetLocationX, catapult.targetLocationY,25));
        }

        //test shoot monster with same nearest but most monster
        Monster m6 = ArenaObjectFactory.createMonster(MonsterType.UNICORN, (short) 430, (short) 10, 1); testList.add(m6);
        Monster m7 = ArenaObjectFactory.createMonster(MonsterType.UNICORN, (short) 370, (short) 50, 1); testList.add(m7);
        for (Monster m : testList) {
            // So they can't move
            m.baseSpeed = 0;
            m.speed = 0;
        }

        simulateNextFrame();
        Assert.assertTrue(catapult.monstersInSplashRange.size() == 2
                && catapult.monstersInSplashRange.get(0) == m3
                && catapult.monstersInSplashRange.get(1) == m6);
        for (Monster m :catapult.monstersInSplashRange) {
            Assert.assertTrue(Geometry.isInCircle( m.getX(), m.getY(), catapult.targetLocationX, catapult.targetLocationY,25));
        }
    }
}