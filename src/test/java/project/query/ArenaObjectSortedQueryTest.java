package project.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;

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
import project.entity.ArenaObjectFactory.MonsterType;
import project.entity.ArenaObjectFactory.TowerType;
import project.query.ArenaObjectStorage.SortOption;
import project.query.ArenaObjectStorage.StoredComparableType;
import project.query.ArenaObjectStorage.StoredType;
import project.util.CollectionComparator;

/**
 * Tests the {@link ArenaObjectSortedQuery} class.
 */
public class ArenaObjectSortedQueryTest extends JavaFXTester {

    DummySortedSelector<?> selectedSortedSelector = null;

    private class DummySortedSelector<T extends ArenaObject & Comparable<T>>
             implements ArenaObjectSortedSelector<T> {

        private int cost;

        private DummySortedSelector(int cost) {
            this.cost = cost;
        }

        @Override
        public int estimateCost(ArenaObjectStorage storage) {
            return cost;
        }

        @Override
        public LinkedList<ArenaObject> select(ArenaObjectStorage storage, EnumSet<StoredType> types,
                LinkedList<ArenaObjectSelector> filters) {

            return null;
        }
        
        @Override
        public PriorityQueue<T> select(ArenaObjectStorage storage, StoredComparableType type,
                LinkedList<ArenaObjectSortedSelector<T>> filters, SortOption option) {

            selectedSortedSelector = this;
            return null;
        }

        @Override
        public boolean isInSelection(ArenaObject o) {
            return true;
        }
    }

    @Test
    public void testConstructor() {
        DummySortedSelector<Monster> s1 = new DummySortedSelector<>(1);
        DummySortedSelector<Monster> s2 = new DummySortedSelector<>(2);
        DummySortedSelector<Monster> s3 = new DummySortedSelector<>(3);

        ArenaObjectSortedQuery<Monster> q1 = new ArenaObjectSortedQuery<>();
        assertTrue(q1.selectors.isEmpty());

        ArenaObjectSortedQuery<Monster> q2 = new ArenaObjectSortedQuery<>(s1);
        assertEquals(1, q2.selectors.size());

        LinkedList<ArenaObjectSortedSelector<Monster>> l = new LinkedList<>(); l.add(s1); l.add(s3); l.add(s2);
        ArenaObjectSortedQuery<Monster> q3 = new ArenaObjectSortedQuery<>(l);
        assertEquals(3, q3.selectors.size());
    }

    @Test
    public void testQuerySequence() {
        ArenaObjectStorage storage = ArenaManager.getActiveObjectStorage();

        DummySortedSelector<Monster> s1 = new DummySortedSelector<>(1);
        DummySortedSelector<Monster> s2 = new DummySortedSelector<>(2);
        DummySortedSelector<Monster> s3 = new DummySortedSelector<>(3);
        
        ArenaObjectSortedQuery<Monster> q = new ArenaObjectSortedQuery<>();
        q.restrict(s3); assertEquals(1, q.selectors.size());
        q.restrict(s3); assertEquals(1, q.selectors.size()); // No duplicates
        q.restrict(s2); assertEquals(2, q.selectors.size());
        q.restrict(s2); assertEquals(2, q.selectors.size()); // No duplicates
        q.restrict(s1); assertEquals(3, q.selectors.size());

        q.run(storage, StoredComparableType.MONSTER, SortOption.ASCENDING);
        assertNull(selectedSortedSelector); // Search by type
        q.run(storage, StoredComparableType.MONSTER, SortOption.DESCENDING);
        assertNull(selectedSortedSelector); // Search by type

        Monster m1 = ArenaObjectFactory.createMonster(this, MonsterType.FOX, ZERO, ZERO, 1);
        q.run(storage, StoredComparableType.MONSTER, SortOption.ASCENDING);
        assertNull(selectedSortedSelector); // Search by type
        q.run(storage, StoredComparableType.MONSTER, SortOption.DESCENDING);
        assertNull(selectedSortedSelector); // Search by type

        Monster m2 = ArenaObjectFactory.createMonster(this, MonsterType.FOX, ZERO, ZERO, 1);
        q.run(storage, StoredComparableType.MONSTER, SortOption.ASCENDING);
        assertEquals(s1, selectedSortedSelector); // Search by selector
        q.run(storage, StoredComparableType.MONSTER, SortOption.DESCENDING);
        assertEquals(s1, selectedSortedSelector); // Search by DESCENDING

        selectedSortedSelector = null; // Reset

        ArenaObjectFactory.removeObject(this, m1);
        q.run(storage, StoredComparableType.MONSTER, SortOption.ASCENDING);
        assertNull(selectedSortedSelector); // Search by type
        q.run(storage, StoredComparableType.MONSTER, SortOption.DESCENDING);
        assertNull(selectedSortedSelector); // Search by type

        ArenaObjectFactory.removeObject(this, m2);
        q.run(storage, StoredComparableType.MONSTER, SortOption.ASCENDING);
        assertNull(selectedSortedSelector); // Search by type
        q.run(storage, StoredComparableType.MONSTER, SortOption.DESCENDING);
        assertNull(selectedSortedSelector); // Search by type

        ArenaObjectFactory.createTower(this, TowerType.BASIC, ZERO, ZERO);
        q.run(storage, StoredComparableType.MONSTER, SortOption.ASCENDING);
        assertNull(selectedSortedSelector); // Search by type
    }

    @Test
    public void testEmptySelector() {
        ArenaObjectStorage storage = ArenaManager.getActiveObjectStorage();

        Monster m1 = ArenaObjectFactory.createMonster(this, MonsterType.PENGUIN, (short) 5, (short) 10, 1);
        Monster m2 = ArenaObjectFactory.createMonster(this, MonsterType.PENGUIN, (short) 25, (short) 10, 1);
        Monster m3 = ArenaObjectFactory.createMonster(this, MonsterType.PENGUIN, (short) 45, (short) 10, 1);
        Monster m4 = ArenaObjectFactory.createMonster(this, MonsterType.PENGUIN, (short) 30, (short) 10, 1);
        Monster m5 = ArenaObjectFactory.createMonster(this, MonsterType.PENGUIN, (short) 65, (short) 10, 1);

        ArenaObjectSortedQuery<Monster> q = new ArenaObjectSortedQuery<>();
        {
            PriorityQueue<Monster> expected = new PriorityQueue<>(Arrays.asList(m1, m2, m3, m4, m5));
            PriorityQueue<Monster> result = q.run(storage, StoredComparableType.MONSTER, SortOption.ASCENDING);
            assertTrue(CollectionComparator.isElementSetAndOrderEqual(expected, result));
        }
        {
            PriorityQueue<Monster> expected = new PriorityQueue<>((o1, o2) -> o2.compareTo(o1));
            expected.addAll(Arrays.asList(m1, m2, m3, m4, m5));
            PriorityQueue<Monster> result = q.run(storage, StoredComparableType.MONSTER, SortOption.DESCENDING);
            assertTrue(CollectionComparator.isElementSetAndOrderEqual(expected, result));
        }
    }
}