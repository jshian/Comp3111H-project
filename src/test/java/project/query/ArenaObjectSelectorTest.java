package project.query;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.PriorityQueue;

import org.junit.Test;

import project.JavaFXTester;
import project.control.ArenaManager;
import project.entity.ArenaObject;
import project.entity.ArenaObjectFactory;
import project.entity.Monster;
import project.entity.Tower;
import project.entity.ArenaObjectFactory.MonsterType;
import project.query.ArenaObjectStorage.SortOption;
import project.query.ArenaObjectStorage.StoredComparableType;
import project.query.ArenaObjectStorage.StoredType;
import project.util.CollectionComparator;

/**
 * Tests the classes that implement {@link ArenaObjectSelector} and {@link ArenaObjectSortedSelector}.
 */
public class ArenaObjectSelectorTest extends JavaFXTester {

    @Test
    public void testBoundaryCases() {

        ArenaObjectStorage storage = ArenaManager.getActiveObjectStorage();
        {
            {
                ArenaObjectFactory.createMonster(this, MonsterType.UNICORN, (short) 12, (short) 49, 1);
                ArenaObjectFactory.createMonster(this, MonsterType.UNICORN, (short) 47, (short) 29, 1);
                ArenaObjectFactory.createMonster(this, MonsterType.UNICORN, (short) 42, (short) 56, 1);
                ArenaObjectFactory.createMonster(this, MonsterType.UNICORN, (short) 50, (short) 20, 1);

                ArenaObjectFactory.createMonster(this, MonsterType.UNICORN, (short) 0, (short) 0, 1);
                ArenaObjectFactory.createMonster(this, MonsterType.UNICORN, ArenaManager.ARENA_WIDTH, (short) 0, 1);
                ArenaObjectFactory.createMonster(this, MonsterType.UNICORN, (short) 0, ArenaManager.ARENA_HEIGHT, 1);
                ArenaObjectFactory.createMonster(this, MonsterType.UNICORN, ArenaManager.ARENA_WIDTH, ArenaManager.ARENA_HEIGHT, 1);
            }

            Monster m1 = ArenaObjectFactory.createMonster(this, MonsterType.UNICORN, (short) 30, (short) 60, 1);
            Monster m2 = ArenaObjectFactory.createMonster(this, MonsterType.UNICORN, (short) 10, (short) 40, 1);
            Monster m3 = ArenaObjectFactory.createMonster(this, MonsterType.UNICORN, (short) 31, (short) 21, 1);
            Monster m4 = ArenaObjectFactory.createMonster(this, MonsterType.UNICORN, (short) 10, (short) 40, 1);
            Monster m5 = ArenaObjectFactory.createMonster(this, MonsterType.UNICORN, (short) 50, (short) 40, 1);

            ArenaObjectCircleSelector circleSelector = new ArenaObjectCircleSelector((short) 30, (short) 40, (short) 20);
            float selectivity = circleSelector.estimateSelectivity(storage);
            assertTrue(0 <= selectivity && selectivity <= 1);

            {
                LinkedList<ArenaObject> result = storage.getQueryResult(circleSelector, EnumSet.of(StoredType.MONSTER));
                LinkedList<ArenaObject> expected = new LinkedList<>(Arrays.asList(m1, m2, m3, m4, m5));
                assertTrue(CollectionComparator.isElementSetEqual(result, expected));
            }
            
            ArenaObjectCircleSortedSelector<Monster> circleSortedSelector = new ArenaObjectCircleSortedSelector<>((short) 30, (short) 40, (short) 20);
            {
                PriorityQueue<Monster> result = storage.getSortedQueryResult(circleSortedSelector, StoredComparableType.MONSTER, SortOption.ASCENDING);
                PriorityQueue<Monster> expected = new PriorityQueue<>(Arrays.asList(m1, m2, m3, m4, m5));
                assertTrue(CollectionComparator.isElementSetAndOrderEqual(result, expected));
            }
            {
                PriorityQueue<Monster> result = storage.getSortedQueryResult(circleSortedSelector, StoredComparableType.MONSTER, SortOption.DESCENDING);
                PriorityQueue<Monster> expected = new PriorityQueue<>((o1, o2) -> o2.compareTo(o1)); expected.addAll(Arrays.asList(m1, m2, m3, m4, m5));
                assertTrue(CollectionComparator.isElementSetAndOrderEqual(result, expected));
            }
        }
    }

    @Test
    public void testGeneralCases() {
        ArenaObjectCircleSelector circleSelector;
        ArenaObjectCircleSortedSelector<Monster> circleSortedSelector;

        ArenaObjectGridSelector gridSelector;
        ArenaObjectGridSortedSelector<Monster> gridSortedSelector;

        ArenaObjectPropertySelector<Tower> propertySelector;
        ArenaObjectPropertySortedSelector<Monster> propertySortedSelector;

        ArenaObjectRectangleSelector rectangleSelector;
        ArenaObjectRectangleSortedSelector<Monster> rectangleSortedSelector;

        ArenaObjectRingSelector ringSelector;
        ArenaObjectRingSortedSelector<Monster> ringSortedSelector;
    }
}