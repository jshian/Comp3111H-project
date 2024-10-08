package project.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import project.JavaFXTester;
import project.control.ArenaManager;
import project.entity.ArenaObject;
import project.entity.ArenaObjectFactory;
import project.entity.Monster;
import project.entity.Projectile;
import project.entity.Tower;
import project.entity.ArenaObjectFactory.MonsterType;
import project.entity.ArenaObjectFactory.TowerType;
import project.query.ArenaObjectStorage.StoredType;
import project.util.CollectionComparator;

/**
 * Tests the {@link ArenaObjectQuery} class.
 */
public class ArenaObjectQueryTest extends JavaFXTester {

    DummySelector selectedSelector = null;

    private class DummySelector implements ArenaObjectSelector {

        private int cost;

        private DummySelector(int cost) {
            this.cost = cost;
        }

        @Override
        public int estimateCost(ArenaObjectStorage storage) {
            return cost;
        }

        @Override
        public List<ArenaObject> select(ArenaObjectStorage storage, EnumSet<StoredType> types,
                List<ArenaObjectSelector> filters) {

            selectedSelector = this;
            return null;
        }

        @Override
        public boolean isInSelection(ArenaObject o) {
            return true;
        }

        @Override
        public boolean isInSelectionByDefinition(ArenaObject o) {
            return true;
        }
    }

    @Test
    public void testConstructor() {
        DummySelector s1 = new DummySelector(1);
        DummySelector s2 = new DummySelector(2);
        DummySelector s3 = new DummySelector(3);

        ArenaObjectQuery q1 = new ArenaObjectQuery();
        assertTrue(q1.selectors.isEmpty());

        ArenaObjectQuery q2 = new ArenaObjectQuery(s1);
        assertEquals(1, q2.selectors.size());

        LinkedList<ArenaObjectSelector> l = new LinkedList<>(); l.add(s1); l.add(s3); l.add(s2);
        ArenaObjectQuery q3 = new ArenaObjectQuery(l);
        assertEquals(3, q3.selectors.size());
    }

    @Test
    public void testQuerySequence() {
        ArenaObjectStorage storage = ArenaManager.getActiveObjectStorage();

        DummySelector s1 = new DummySelector(1);
        DummySelector s2 = new DummySelector(2);
        DummySelector s3 = new DummySelector(3);

        ArenaObjectQuery q = new ArenaObjectQuery();
        q.restrict(s1); assertEquals(1, q.selectors.size());
        q.restrict(s3); assertEquals(2, q.selectors.size());
        q.restrict(s1); assertEquals(2, q.selectors.size()); // No duplicates
        q.restrict(s2); assertEquals(3, q.selectors.size());

        q.run(storage, EnumSet.noneOf(StoredType.class));
        assertNull(selectedSelector); // Search by type

        q.run(storage, EnumSet.of(StoredType.MONSTER));
        assertNull(selectedSelector); // Search by type

        Monster m1 = ArenaObjectFactory.createMonster(this, MonsterType.FOX, ZERO, ZERO, 1);
        q.run(storage, EnumSet.of(StoredType.MONSTER));
        assertNull(selectedSelector); // Search by type

        Monster m2 = ArenaObjectFactory.createMonster(this, MonsterType.FOX, ZERO, ZERO, 1);
        q.run(storage, EnumSet.of(StoredType.MONSTER));
        assertEquals(s1, selectedSelector); // Search by selector
        
        selectedSelector = null; // Reset

        ArenaObjectFactory.removeObject(this, m1);
        q.run(storage, EnumSet.of(StoredType.MONSTER));
        assertNull(selectedSelector); // Search by type

        ArenaObjectFactory.removeObject(this, m2);
        q.run(storage, EnumSet.of(StoredType.MONSTER));
        assertNull(selectedSelector); // Search by type
        
        ArenaObjectFactory.createTower(this, TowerType.BASIC, ZERO, ZERO);
        q.run(storage, EnumSet.noneOf(StoredType.class));
        assertNull(selectedSelector); // Search by type
    }

    @Test
    public void testEmptySelector() {
        ArenaObjectStorage storage = ArenaManager.getActiveObjectStorage();

        Monster m = ArenaObjectFactory.createMonster(this, MonsterType.PENGUIN, (short) 5, (short) 10, 1);
        Tower t1 = ArenaObjectFactory.createTower(this, TowerType.CATAPULT, (short) 25, (short) 50);
        Projectile p = ArenaObjectFactory.createProjectile(this, t1, m, (short) 0, (short) 0);
        Tower t2 = ArenaObjectFactory.createTower(this, TowerType.LASER, (short) 125, (short) 350);

        ArenaObjectQuery q = new ArenaObjectQuery();
        {
            List<ArenaObject> result = q.run(storage, EnumSet.noneOf(StoredType.class));
            assertTrue(result.isEmpty());
        }
        {
            List<ArenaObject> expected = new LinkedList<>(); expected.add(m);
            List<ArenaObject> result = q.run(storage, EnumSet.of(StoredType.MONSTER));
            assertTrue(CollectionComparator.isElementSetEqual(expected, result));
        }
        {
            List<ArenaObject> expected = new LinkedList<>(); expected.add(t1); expected.add(t2);
            List<ArenaObject> result = q.run(storage, EnumSet.of(StoredType.TOWER));
            assertTrue(CollectionComparator.isElementSetEqual(expected, result));
        }
        {
            List<ArenaObject> expected = new LinkedList<>(); expected.add(p);
            List<ArenaObject> result = q.run(storage, EnumSet.of(StoredType.PROJECTILE));
            assertTrue(CollectionComparator.isElementSetEqual(expected, result));
        }
    }
}