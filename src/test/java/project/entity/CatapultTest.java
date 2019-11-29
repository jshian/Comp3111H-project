package project.entity;

import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import project.JavaFXTester;
import project.entity.Monster;
import project.entity.ArenaObjectFactory.MonsterType;
import project.entity.ArenaObjectFactory.TowerType;
import project.util.CollectionComparator;
import project.util.Geometry;

/**
 * Tests the {@link Catapult} class.
 */
public class CatapultTest extends JavaFXTester {

    @Test
    public void testShootingRangeValid() {
        List<Monster> testList = new LinkedList<>();

        Catapult catapult = (Catapult) ArenaObjectFactory.createTower(this, TowerType.CATAPULT, (short) 370, (short) 10);
        Monster m1 = ArenaObjectFactory.createMonster(this, MonsterType.UNICORN, (short) 110, (short) 110, 1); testList.add(m1);
        Monster m2 = ArenaObjectFactory.createMonster(this, MonsterType.UNICORN, (short) 130, (short) 110, 1); testList.add(m2);
        Monster m3 = ArenaObjectFactory.createMonster(this, MonsterType.UNICORN, (short) 450, (short) 10, 1); testList.add(m3);
        
        for (Monster m : testList) {
            // So they can't move
            m.baseSpeed = 0;
            m.speed = 0;
        }

        simulateNextFrameNoSpawning();

        List<Monster> answer = new LinkedList<>(); answer.add(m3);
        Assert.assertTrue(CollectionComparator.isElementSetEqual(catapult.monstersInSplashRange, answer));
        for (Monster m :catapult.monstersInSplashRange) {
            Assert.assertTrue(Geometry.isInCircle( m.getX(), m.getY(), catapult.targetLocationX, catapult.targetLocationY,25));
        }
    }

    @Test
    public void testShootNearest() {
        List<Monster> testList = new LinkedList<>();

        Catapult catapult = (Catapult) ArenaObjectFactory.createTower(this, TowerType.CATAPULT, (short) 370, (short) 10);
        Monster m1 = ArenaObjectFactory.createMonster(this, MonsterType.UNICORN, (short) 110, (short) 110, 1); testList.add(m1);
        Monster m2 = ArenaObjectFactory.createMonster(this, MonsterType.UNICORN, (short) 130, (short) 110, 1); testList.add(m2);
        Monster m3 = ArenaObjectFactory.createMonster(this, MonsterType.UNICORN, (short) 450, (short) 10, 1); testList.add(m3);
        
        //test shoot monster with nearest but not most monsters
        Monster m4 = ArenaObjectFactory.createMonster(this, MonsterType.UNICORN, (short) 330, (short) 10, 1); testList.add(m4);
        Monster m5 = ArenaObjectFactory.createMonster(this, MonsterType.UNICORN, (short) 330, (short) 10, 1); testList.add(m5);

        for (Monster m : testList) {
            // So they can't move
            m.baseSpeed = 0;
            m.speed = 0;
        }

        simulateNextFrameNoSpawning();

        List<Monster> answer = new LinkedList<>(); answer.add(m3);
        Assert.assertTrue(CollectionComparator.isElementSetEqual(catapult.monstersInSplashRange, answer));
        for (Monster m :catapult.monstersInSplashRange) {
            Assert.assertTrue(Geometry.isInCircle( m.getX(), m.getY(), catapult.targetLocationX, catapult.targetLocationY,25));
        }
    }

    @Test
    public void testShootMost() {
        List<Monster> testList = new LinkedList<>();

        Catapult catapult = (Catapult) ArenaObjectFactory.createTower(this, TowerType.CATAPULT, (short) 370, (short) 10);
        Monster m1 = ArenaObjectFactory.createMonster(this, MonsterType.UNICORN, (short) 110, (short) 110, 1); testList.add(m1);
        Monster m2 = ArenaObjectFactory.createMonster(this, MonsterType.UNICORN, (short) 130, (short) 110, 1); testList.add(m2);
        Monster m3 = ArenaObjectFactory.createMonster(this, MonsterType.UNICORN, (short) 450, (short) 10, 1); testList.add(m3);
        Monster m4 = ArenaObjectFactory.createMonster(this, MonsterType.UNICORN, (short) 330, (short) 10, 1); testList.add(m4);
        Monster m5 = ArenaObjectFactory.createMonster(this, MonsterType.UNICORN, (short) 330, (short) 10, 1); testList.add(m5);
 
        //test shoot monster with same nearest but most monster
        Monster m6 = ArenaObjectFactory.createMonster(this, MonsterType.UNICORN, (short) 430, (short) 10, 1); testList.add(m6);
        Monster m7 = ArenaObjectFactory.createMonster(this, MonsterType.UNICORN, (short) 370, (short) 50, 1); testList.add(m7);
        
        for (Monster m : testList) {
            // So they can't move
            m.baseSpeed = 0;
            m.speed = 0;
        }

        simulateNextFrameNoSpawning();

        List<Monster> answer = new LinkedList<>(); answer.add(m3); answer.add(m6);
        Assert.assertTrue(CollectionComparator.isElementSetEqual(catapult.monstersInSplashRange, answer));
        for (Monster m :catapult.monstersInSplashRange) {
            Assert.assertTrue(Geometry.isInCircle( m.getX(), m.getY(), catapult.targetLocationX, catapult.targetLocationY,25));
        }
    }
}