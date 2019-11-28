package project.query;

import static org.junit.Assert.assertTrue;

import java.util.PriorityQueue;
import java.util.Random;

import org.junit.Test;

import project.JavaFXTester;
import project.control.ArenaManager;
import project.entity.ArenaObjectFactory;
import project.entity.Monster;
import project.entity.ArenaObjectFactory.MonsterType;
import project.query.ArenaObjectStorage.SortOption;
import project.query.ArenaObjectStorage.StoredComparableType;
import project.util.CollectionComparator;

/**
 * Tests the {@link ArenaObjectRectangleSortedSelector} class.
 */
public class ArenaObjectRectangleSortedSelectorTest extends JavaFXTester {
    // The objects to be tested
    private PriorityQueue<Monster> expectedSelection = new PriorityQueue<>();

    // Number of random test cases
    private static int NUM_RANDOM_SELECTORS = 100;
    private static int NUM_RANDOM_MONSTERS = 100;

    private PriorityQueue<Monster> generateBox(short leftX, short topY, short width, short height) {
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

        PriorityQueue<Monster> monsters = new PriorityQueue<>();
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
        
        expectedSelection.addAll(generateBox((short) 50, (short) 10, (short) 290, (short) 270));
        generateBox((short) 49, (short) 9, (short) 292, (short) 272);
        
        ArenaObjectRectangleSortedSelector<Monster> rectangleSortedSelector = new ArenaObjectRectangleSortedSelector<>((short) 50, (short) 10, (short) 290, (short) 270);
        int cost = rectangleSortedSelector.estimateCost(storage);
        assertTrue(cost > 0);
        {
            PriorityQueue<Monster> result = storage.getSortedQueryResult(rectangleSortedSelector, StoredComparableType.MONSTER, SortOption.ASCENDING);
            PriorityQueue<Monster> expected = new PriorityQueue<>(expectedSelection);
            assertTrue(CollectionComparator.isElementSetAndOrderEqual(expected, result));
        }
        {
            PriorityQueue<Monster> result = storage.getSortedQueryResult(rectangleSortedSelector, StoredComparableType.MONSTER, SortOption.DESCENDING);
            PriorityQueue<Monster> expected = new PriorityQueue<>((o1, o2) -> o2.compareTo(o1)); expected.addAll(expectedSelection);
            assertTrue(CollectionComparator.isElementSetAndOrderEqual(expected, result));
        }
    }

    private void reset() {
        System.out.println("Resetting...");
        expectedSelection.clear();
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

            ArenaObjectRectangleSortedSelector<Monster> rectangleSortedSelector = new ArenaObjectRectangleSortedSelector<>(leftX, topY, width, height);
            System.out.println(String.format("Created rectangle selector: leftX = %d, topY = %d, width = %d, height = %d", leftX, topY, width, height));
            int cost = rectangleSortedSelector.estimateCost(ArenaManager.getActiveObjectStorage());
            assertTrue(cost > 0);

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
                PriorityQueue<Monster> result = ArenaManager.getActiveObjectStorage().getSortedQueryResult(rectangleSortedSelector, StoredComparableType.MONSTER, SortOption.ASCENDING);
                PriorityQueue<Monster> expected = new PriorityQueue<>(expectedSelection);
                assertTrue(CollectionComparator.isElementSetAndOrderEqual(expected, result));
            }
            {
                PriorityQueue<Monster> result = ArenaManager.getActiveObjectStorage().getSortedQueryResult(rectangleSortedSelector, StoredComparableType.MONSTER, SortOption.DESCENDING);
                PriorityQueue<Monster> expected = new PriorityQueue<>((o1, o2) -> o2.compareTo(o1)); expected.addAll(expectedSelection);
                assertTrue(CollectionComparator.isElementSetAndOrderEqual(expected, result));
            }
        }
    }
}