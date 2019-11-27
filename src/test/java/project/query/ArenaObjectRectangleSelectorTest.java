package project.query;

import static org.junit.Assert.assertTrue;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.Random;

import org.junit.Test;

import project.JavaFXTester;
import project.control.ArenaManager;
import project.entity.ArenaObject;
import project.entity.ArenaObjectFactory;
import project.entity.Monster;
import project.entity.ArenaObjectFactory.MonsterType;
import project.query.ArenaObjectStorage.StoredType;
import project.util.CollectionComparator;

/**
 * Tests the {@link ArenaObjectRectangleSelector} class.
 */
public class ArenaObjectRectangleSelectorTest extends JavaFXTester {
    // The objects to be tested
    private LinkedList<Monster> expectedSelection = new LinkedList<>();

    // Number of random test cases
    private static int NUM_RANDOM_SELECTORS = 100;
    private static int NUM_RANDOM_MONSTERS = 100;

    private LinkedList<Monster> generateMonsterBox(short leftX, short topY, short width, short height) {
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

        Random rng = new Random();

        LinkedList<Monster> monsters = new LinkedList<>();
        for (short x = leftX; x <= rightX; x++) {
            monsters.add(ArenaObjectFactory.createMonster(this, MonsterType.values()[rng.nextInt(MonsterType.values().length)], x, topY, 1));
            monsters.add(ArenaObjectFactory.createMonster(this, MonsterType.values()[rng.nextInt(MonsterType.values().length)], x, bottomY, 1));
        }
        for (short y = topY; y <= bottomY; y++) {
            monsters.add(ArenaObjectFactory.createMonster(this, MonsterType.values()[rng.nextInt(MonsterType.values().length)], leftX, y, 1));
            monsters.add(ArenaObjectFactory.createMonster(this, MonsterType.values()[rng.nextInt(MonsterType.values().length)], rightX, y, 1));
        }

        return monsters;
    }

    @Test
    public void testBoundaryCases() {
        ArenaObjectStorage storage = ArenaManager.getActiveObjectStorage();
        
        expectedSelection.addAll(generateMonsterBox((short) 100, (short) 200, (short) 50, (short) 40));
        generateMonsterBox((short) 99, (short) 199, (short) 52, (short) 42);

        ArenaObjectRectangleSelector rectangleSelector = new ArenaObjectRectangleSelector((short) 100, (short) 200, (short) 50, (short) 40);
        float selectivity = rectangleSelector.estimateSelectivity(storage);
        assertTrue(0 <= selectivity && selectivity <= 1);

        {
            LinkedList<ArenaObject> result = storage.getQueryResult(rectangleSelector, EnumSet.of(StoredType.MONSTER));
            LinkedList<ArenaObject> expected = new LinkedList<>(expectedSelection);
            assertTrue(CollectionComparator.isElementSetEqual(result, expected));
        }
    }

    private void reset() {
        expectedSelection.clear();
        ArenaManager.getActiveObjectStorage().clear();
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

            reset();
            for (int j = 0; j < NUM_RANDOM_MONSTERS; j++) {
                Monster m = ArenaObjectFactory.createMonster(this,
                    MonsterType.values()[rng.nextInt(MonsterType.values().length)],
                    (short) rng.nextInt(ArenaManager.ARENA_WIDTH + 1),
                    (short) rng.nextInt(ArenaManager.ARENA_HEIGHT + 1), 1
                );
    
                if (m.getX() >= leftX && m.getX() <= rightX && m.getY() >= topY && m.getY() <= bottomY) {
                    expectedSelection.add(m);
                    System.out.println(String.format("Added valid monster: x = %d, y = %d", m.getX(), m.getY()));
                } else {
                    System.out.println(String.format("Added invalid monster: x = %d, y = %d", m.getX(), m.getY()));
                }
            }

            {
                System.out.println("Testing...");
                LinkedList<ArenaObject> result = ArenaManager.getActiveObjectStorage().getQueryResult(rectangleSelector, EnumSet.of(StoredType.MONSTER));
                LinkedList<ArenaObject> expected = new LinkedList<>(expectedSelection);
                assertTrue(CollectionComparator.isElementSetEqual(result, expected));
            }
        }
    }
}