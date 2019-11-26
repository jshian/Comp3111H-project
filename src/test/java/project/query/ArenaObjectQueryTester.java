package project.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.PriorityQueue;

import org.junit.Test;

import project.JavaFXTester;
import project.control.ArenaManager;
import project.entity.ArenaObject;
import project.entity.Monster;
import project.query.ArenaObjectStorage.SortOption;
import project.query.ArenaObjectStorage.StoredComparableType;
import project.query.ArenaObjectStorage.StoredType;

/**
 * Tests the {@link ArenaObjectQuery} and {@link ArenaObjectSortedQuery} classes.
 */
public class ArenaObjectQueryTester extends JavaFXTester {

    DummySelector selectedSelector = null;

    private class DummySelector implements ArenaObjectSelector {

        protected float selectivity;

        protected DummySelector(float selectivity) {
            this.selectivity = selectivity;
        }

        @Override
        public float estimateSelectivity(ArenaObjectStorage storage) {
            return selectivity;
        }

        @Override
        public LinkedList<ArenaObject> select(ArenaObjectStorage storage, EnumSet<StoredType> types,
                LinkedList<ArenaObjectSelector> filters) {

            selectedSelector = this;
            return null;
        }

        @Override
        public boolean isInSelection(ArenaObject o) {
            return true;
        }
    }

    DummySortedSelector<?> selectedSortedSelector = null;

    private class DummySortedSelector<T extends ArenaObject & Comparable<T>>
            extends DummySelector implements ArenaObjectSortedSelector<T> {

        protected DummySortedSelector(float selectivity) {
            super(selectivity);
        }
        
        @Override
        public PriorityQueue<T> select(ArenaObjectStorage storage, StoredComparableType type,
                LinkedList<ArenaObjectSortedSelector<T>> filters, SortOption option) {

            selectedSortedSelector = this;
            return null;
        }
    }

    @Test
    public void testArenaObjectQuery() {

        ArenaObjectStorage storage = ArenaManager.getActiveObjectStorage();

        DummySelector s1 = new DummySelector(0.1f);
        DummySelector s2 = new DummySelector(0.2f);
        DummySelector s3 = new DummySelector(0.3f);

        DummySortedSelector<Monster> ss1 = new DummySortedSelector<>(0.1f);
        DummySortedSelector<Monster> ss2 = new DummySortedSelector<>(0.2f);
        DummySortedSelector<Monster> ss3 = new DummySortedSelector<>(0.3f);

        // Test constructors
        {
            ArenaObjectQuery q1 = new ArenaObjectQuery();
            assertTrue(q1.selectors.isEmpty());

            ArenaObjectQuery q2 = new ArenaObjectQuery(s1);
            assertTrue(q2.selectors.size() == 1);

            LinkedList<ArenaObjectSelector> l = new LinkedList<>(); l.add(s1); l.add(s3);
            ArenaObjectQuery q3 = new ArenaObjectQuery(l);
            assertTrue(q3.selectors.size() == 2);
        }

        // Test query sequence
        {
            ArenaObjectQuery q = new ArenaObjectQuery();
            q.restrict(s1); assertTrue(q.selectors.size() == 1);
            q.restrict(s3); assertTrue(q.selectors.size() == 2);
            q.restrict(s1); assertTrue(q.selectors.size() == 2); // No duplicates
            q.restrict(s2); assertTrue(q.selectors.size() == 3);
            q.run(storage, EnumSet.noneOf(StoredType.class));
            assertEquals(selectedSelector, s1);
            
            ArenaObjectSortedQuery<Monster> sq = new ArenaObjectSortedQuery<>();
            sq.restrict(ss3); assertTrue(sq.selectors.size() == 1);
            sq.restrict(ss3); assertTrue(sq.selectors.size() == 1); // No duplicates
            sq.restrict(ss2); assertTrue(sq.selectors.size() == 2);
            sq.restrict(ss2); assertTrue(sq.selectors.size() == 2); // No duplicates
            sq.restrict(ss1); assertTrue(sq.selectors.size() == 3);
            sq.run(storage, StoredComparableType.MONSTER, SortOption.ASCENDING);
            assertEquals(selectedSortedSelector, ss1);
        }
    }
}