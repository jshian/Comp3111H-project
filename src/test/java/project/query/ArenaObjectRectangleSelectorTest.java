package project.query;

import static org.junit.Assert.assertTrue;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.PriorityQueue;

import org.junit.Test;

import project.JavaFXTester;
import project.control.ArenaManager;
import project.entity.ArenaObject;
import project.entity.ArenaObjectFactory;
import project.entity.Monster;
import project.entity.ArenaObjectFactory.MonsterType;
import project.query.ArenaObjectStorage.SortOption;
import project.query.ArenaObjectStorage.StoredComparableType;
import project.query.ArenaObjectStorage.StoredType;
import project.util.CollectionComparator;

/**
 * Tests the {@link ArenaObjectRectangleSelector} and {@link ArenaObjectRectangleSortedSelector} class.
 */
public class ArenaObjectRectangleSelectorTest extends JavaFXTester {

    private LinkedList<Monster> addMonstersAtEdges(short leftX, short topY, short width, short height) {
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

        LinkedList<Monster> monsters = new LinkedList<>();
        for (short x = leftX; x <= rightX; x++) {
            monsters.add(ArenaObjectFactory.createMonster(this, MonsterType.UNICORN, x, topY, 1));
            monsters.add(ArenaObjectFactory.createMonster(this, MonsterType.UNICORN, x, bottomY, 1));
        }
        for (short y = topY; y <= bottomY; y++) {
            monsters.add(ArenaObjectFactory.createMonster(this, MonsterType.UNICORN, leftX, y, 1));
            monsters.add(ArenaObjectFactory.createMonster(this, MonsterType.UNICORN, rightX, y, 1));
        }
        
        return monsters;
    }

    @Test
    public void testBoundaryCases() {

        ArenaObjectStorage storage = ArenaManager.getActiveObjectStorage();
        
        LinkedList<Monster> monstersAtEdge = addMonstersAtEdges((short) 100, (short) 200, (short) 50, (short) 40);
        LinkedList<Monster> monstersOutsideEdge = addMonstersAtEdges((short) 99, (short) 199, (short) 52, (short) 42);

        ArenaObjectRectangleSelector rectangleSelector = new ArenaObjectRectangleSelector((short) 100, (short) 200, (short) 50, (short) 40);
        float selectivity = rectangleSelector.estimateSelectivity(storage);
        assertTrue(0 <= selectivity && selectivity <= 1);

        {
            LinkedList<ArenaObject> result = storage.getQueryResult(rectangleSelector, EnumSet.of(StoredType.MONSTER));
            LinkedList<ArenaObject> expected = new LinkedList<>(monstersAtEdge);
            assertTrue(CollectionComparator.isElementSetEqual(result, expected));
        }
        
        ArenaObjectRectangleSortedSelector<Monster> rectangleSortedSelector = new ArenaObjectRectangleSortedSelector<>((short) 100, (short) 200, (short) 50, (short) 40);
        {
            PriorityQueue<Monster> result = storage.getSortedQueryResult(rectangleSortedSelector, StoredComparableType.MONSTER, SortOption.ASCENDING);
            PriorityQueue<Monster> expected = new PriorityQueue<>(monstersAtEdge);
            assertTrue(CollectionComparator.isElementSetAndOrderEqual(result, expected));
        }
        {
            PriorityQueue<Monster> result = storage.getSortedQueryResult(rectangleSortedSelector, StoredComparableType.MONSTER, SortOption.DESCENDING);
            PriorityQueue<Monster> expected = new PriorityQueue<>((o1, o2) -> o2.compareTo(o1)); expected.addAll(monstersAtEdge);
            assertTrue(CollectionComparator.isElementSetAndOrderEqual(result, expected));
        }
    }

    @Test
    public void testGeneralCases() {
        
    }
}