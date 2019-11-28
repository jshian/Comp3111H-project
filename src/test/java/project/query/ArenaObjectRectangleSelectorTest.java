package project.query;

import static org.junit.Assert.assertTrue;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.Random;

import org.junit.Test;

import project.JavaFXTester;
import project.control.ArenaManager;
import project.entity.ArenaObject;
import project.entity.Monster;
import project.entity.Projectile;
import project.entity.Tower;
import project.query.ArenaObjectStorage.StoredType;
import project.util.CollectionComparator;

/**
 * Tests the {@link ArenaObjectRectangleSelector} class.
 */
public class ArenaObjectRectangleSelectorTest extends JavaFXTester {
    // The objects to be tested
    private LinkedList<Tower> expectedTowers = new LinkedList<>();
    private LinkedList<Monster> expectedMonsters = new LinkedList<>();
    private LinkedList<Projectile> expectedProjectiles = new LinkedList<>();

    // Number of random test cases
    private static int NUM_RANDOM_SELECTORS = 20;
    private static int NUM_RANDOM_OBJECTS_PER_TYPE = 20;

    private void addObjects(short leftX, short rightX, short topY, short bottomY, boolean isExpected) {
        Tower t = ArenaObjectStorageHelper.addTower(this);
        if (t.getX() >= leftX && t.getX() <= rightX && t.getY() >= topY && t.getY() <= bottomY) {
            if (isExpected) expectedTowers.add(t);
        }

        Monster m = ArenaObjectStorageHelper.addMonster(this);
        if (m.getX() >= leftX && m.getX() <= rightX && m.getY() >= topY && m.getY() <= bottomY) {
            if (isExpected) expectedMonsters.add(m);
        }

        Projectile p = ArenaObjectStorageHelper.addProjectile(this, t, m);
        if (p.getX() >= leftX && p.getX() <= rightX && p.getY() >= topY && p.getY() <= bottomY) {
            if (isExpected) expectedProjectiles.add(p);
        }
    }

    private void addObjects(short x, short y, short leftX, short rightX, short topY, short bottomY, boolean isExpected) {
        Tower t = ArenaObjectStorageHelper.addTower(this, x, y);
        if (t.getX() >= leftX && t.getX() <= rightX && t.getY() >= topY && t.getY() <= bottomY) {
            if (isExpected) expectedTowers.add(t);
        }

        Monster m = ArenaObjectStorageHelper.addMonster(this, x, y);
        if (m.getX() >= leftX && m.getX() <= rightX && m.getY() >= topY && m.getY() <= bottomY) {
            if (isExpected) expectedMonsters.add(m);
        }

        Projectile p = ArenaObjectStorageHelper.addProjectile(this, t, m, x, y);
        if (p.getX() >= leftX && p.getX() <= rightX && p.getY() >= topY && p.getY() <= bottomY) {
            if (isExpected) expectedProjectiles.add(p);
        }
    }

    private void generateBox(short leftX, short topY, short width, short height, boolean isExpected) {
        if (leftX < 0) leftX = 0;
        if (leftX > ArenaManager.ARENA_WIDTH) leftX = ArenaManager.ARENA_WIDTH;
        if (topY < 0) topY = 0;
        if (topY > ArenaManager.ARENA_HEIGHT) topY = ArenaManager.ARENA_HEIGHT;

        short rightX = (short) (leftX + width);
        if (rightX < 0) rightX = 0;
        if (rightX > ArenaManager.ARENA_WIDTH) rightX = ArenaManager.ARENA_WIDTH;

        short bottomY = (short) (topY + height);
        if (bottomY < 0) bottomY = 0;
        if (bottomY > ArenaManager.ARENA_HEIGHT) bottomY = ArenaManager.ARENA_HEIGHT;

        for (short x = leftX; x <= rightX; x++) {
            addObjects(x, topY, leftX, rightX, topY, bottomY, isExpected);
            addObjects(x, bottomY, leftX, rightX, topY, bottomY, isExpected);
        }
        for (short y = topY; y <= bottomY; y++) {
            addObjects(leftX, y, leftX, rightX, topY, bottomY, isExpected);
            addObjects(rightX, y, leftX, rightX, topY, bottomY, isExpected);
        }
    }

    private void testQueryFilters(ArenaObjectSelector selector) {
        ArenaObjectStorage storage = ArenaManager.getActiveObjectStorage();

        {
            LinkedList<ArenaObject> result = storage.getQueryResult(selector, EnumSet.of(StoredType.MONSTER));
            LinkedList<ArenaObject> expected = new LinkedList<>(expectedMonsters);
            assertTrue(CollectionComparator.isElementSetEqual(result, expected));
        }
        {
            LinkedList<ArenaObject> result = storage.getQueryResult(selector, EnumSet.of(StoredType.TOWER));
            LinkedList<ArenaObject> expected = new LinkedList<>(expectedTowers);
            assertTrue(CollectionComparator.isElementSetEqual(result, expected));
        }
        {
            LinkedList<ArenaObject> result = storage.getQueryResult(selector, EnumSet.of(StoredType.PROJECTILE));
            LinkedList<ArenaObject> expected = new LinkedList<>(expectedProjectiles);
            assertTrue(CollectionComparator.isElementSetEqual(result, expected));
        }
        {
            LinkedList<ArenaObject> result = storage.getQueryResult(selector, EnumSet.of(StoredType.MONSTER, StoredType.TOWER));
            LinkedList<ArenaObject> expected = new LinkedList<>(expectedMonsters); expected.addAll(expectedTowers);
            assertTrue(CollectionComparator.isElementSetEqual(result, expected));
        }
        {
            LinkedList<ArenaObject> result = storage.getQueryResult(selector, EnumSet.of(StoredType.MONSTER, StoredType.PROJECTILE));
            LinkedList<ArenaObject> expected = new LinkedList<>(expectedMonsters); expected.addAll(expectedProjectiles);
            assertTrue(CollectionComparator.isElementSetEqual(result, expected));
        }
        {
            LinkedList<ArenaObject> result = storage.getQueryResult(selector, EnumSet.of(StoredType.TOWER, StoredType.PROJECTILE));
            LinkedList<ArenaObject> expected = new LinkedList<>(expectedTowers); expected.addAll(expectedProjectiles);
            assertTrue(CollectionComparator.isElementSetEqual(result, expected));
        }
        {
            LinkedList<ArenaObject> result = storage.getQueryResult(selector, EnumSet.of(StoredType.TOWER, StoredType.MONSTER, StoredType.PROJECTILE));
            LinkedList<ArenaObject> expected = new LinkedList<>(expectedTowers); expected.addAll(expectedMonsters); expected.addAll(expectedProjectiles);
            assertTrue(CollectionComparator.isElementSetEqual(result, expected));
        }
    }

    @Test
    public void testBoundaryCases() {
        ArenaObjectStorage storage = ArenaManager.getActiveObjectStorage();
        
        generateBox((short) 100, (short) 200, (short) 50, (short) 40, true);
        generateBox((short) 99, (short) 199, (short) 52, (short) 42, false);

        ArenaObjectRectangleSelector rectangleSelector = new ArenaObjectRectangleSelector((short) 100, (short) 200, (short) 50, (short) 40);
        int cost = rectangleSelector.estimateCost(storage);
        assertTrue(cost > 0);

        testQueryFilters(rectangleSelector);
    }

    private void reset() {
        System.out.println("Resetting...");
        expectedTowers.clear();
        expectedMonsters.clear();
        expectedProjectiles.clear();
        ArenaManager.getActiveObjectStorage().clear();
        System.out.println("Reset complete");
    }

    @Test
    public void testGeneralCases() {
        Random rng = new Random();
        for (int i = 0; i < NUM_RANDOM_SELECTORS; i++) {
            short leftX = (short) rng.nextInt(ArenaManager.ARENA_WIDTH + 1);
            if (leftX < 0) leftX = 0;
            if (leftX > ArenaManager.ARENA_WIDTH) leftX = ArenaManager.ARENA_WIDTH;
            
            short topY = (short) rng.nextInt(ArenaManager.ARENA_HEIGHT + 1);
            if (topY < 0) topY = 0;
            if (topY > ArenaManager.ARENA_HEIGHT) topY = ArenaManager.ARENA_HEIGHT;

            short width = (short) rng.nextInt(ArenaManager.ARENA_WIDTH + 1);
            short height = (short) rng.nextInt(ArenaManager.ARENA_HEIGHT + 1);
    
            short rightX = (short) (leftX + width);
            if (rightX < 0) rightX = 0;
            if (rightX > ArenaManager.ARENA_WIDTH) rightX = ArenaManager.ARENA_WIDTH;
    
            short bottomY = (short) (topY + height);
            if (bottomY < 0) bottomY = 0;
            if (bottomY > ArenaManager.ARENA_HEIGHT) bottomY = ArenaManager.ARENA_HEIGHT;

            ArenaObjectRectangleSelector rectangleSelector = new ArenaObjectRectangleSelector(leftX, topY, width, height);
            System.out.println(String.format("Created rectangle selector: leftX = %d, topY = %d, width = %d, height = %d", leftX, topY, width, height));
            int cost = rectangleSelector.estimateCost(ArenaManager.getActiveObjectStorage());
            assertTrue(cost > 0);

            reset();
            for (int j = 0; j < NUM_RANDOM_OBJECTS_PER_TYPE; j++) {
                addObjects(leftX, rightX, topY, bottomY, true);
            }

            {
                System.out.println("Testing...");
                testQueryFilters(rectangleSelector);
            }
        }
    }
}