package project.query;

import static org.junit.Assert.assertEquals;
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
import project.entity.ArenaObjectFactory.MonsterType;
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

        private float selectivity;

        private DummySortedSelector(float selectivity) {
            this.selectivity = selectivity;
        }

        @Override
        public float estimateSelectivity(ArenaObjectStorage storage) {
            return selectivity;
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
        DummySortedSelector<Monster> s1 = new DummySortedSelector<>(0.1f);
        DummySortedSelector<Monster> s2 = new DummySortedSelector<>(0.2f);
        DummySortedSelector<Monster> s3 = new DummySortedSelector<>(0.3f);

        ArenaObjectSortedQuery<Monster> q1 = new ArenaObjectSortedQuery<>();
        assertTrue(q1.selectors.isEmpty());

        ArenaObjectSortedQuery<Monster> q2 = new ArenaObjectSortedQuery<>(s1);
        assertEquals(q2.selectors.size(), 1);

        LinkedList<ArenaObjectSortedSelector<Monster>> l = new LinkedList<>(); l.add(s1); l.add(s3); l.add(s2);
        ArenaObjectSortedQuery<Monster> q3 = new ArenaObjectSortedQuery<>(l);
        assertEquals(q3.selectors.size(), 3);
    }

    @Test
    public void testQuerySequence() {
        ArenaObjectStorage storage = ArenaManager.getActiveObjectStorage();

        DummySortedSelector<Monster> s1 = new DummySortedSelector<>(0.1f);
        DummySortedSelector<Monster> s2 = new DummySortedSelector<>(0.2f);
        DummySortedSelector<Monster> s3 = new DummySortedSelector<>(0.3f);
        
        ArenaObjectSortedQuery<Monster> q = new ArenaObjectSortedQuery<>();
        q.restrict(s3); assertEquals(q.selectors.size(), 1);
        q.restrict(s3); assertEquals(q.selectors.size(), 1); // No duplicates
        q.restrict(s2); assertEquals(q.selectors.size(), 2);
        q.restrict(s2); assertEquals(q.selectors.size(), 2); // No duplicates
        q.restrict(s1); assertEquals(q.selectors.size(), 3);
        q.run(storage, StoredComparableType.MONSTER, SortOption.ASCENDING);
        assertEquals(selectedSortedSelector, s1);
        q.run(storage, StoredComparableType.MONSTER, SortOption.DESCENDING);
        assertEquals(selectedSortedSelector, s1);
    }

    @Test
    public void testEmptySelector() {
        ArenaObjectStorage storage = ArenaManager.getActiveObjectStorage();

        Monster m1 = ArenaObjectFactory.createMonster(this, MonsterType.PENGUIN, (short) 5, (short) 10, 1);
        Monster m2 = ArenaObjectFactory.createMonster(this, MonsterType.PENGUIN, (short) 25, (short) 10, 1);
        Monster m3 = ArenaObjectFactory.createMonster(this, MonsterType.PENGUIN, (short) 45, (short) 10, 1);
        Monster m4 = ArenaObjectFactory.createMonster(this, MonsterType.PENGUIN, (short) 30, (short) 10, 1);

        ArenaObjectSortedQuery<Monster> q = new ArenaObjectSortedQuery<>();
        {
            PriorityQueue<Monster> result = q.run(storage, StoredComparableType.MONSTER, SortOption.ASCENDING);
            PriorityQueue<Monster> actual = new PriorityQueue<>(Arrays.asList(m1, m2, m3, m4));
            assertTrue(CollectionComparator.isElementSetAndOrderEqual(result, actual));
        }
        {
            PriorityQueue<Monster> result = q.run(storage, StoredComparableType.MONSTER, SortOption.DESCENDING);
            PriorityQueue<Monster> actual = new PriorityQueue<>((o1, o2) -> o2.compareTo(o1)); actual.addAll(Arrays.asList(m1, m2, m3, m4));
            assertTrue(CollectionComparator.isElementSetAndOrderEqual(result, actual));
        }
    }
}