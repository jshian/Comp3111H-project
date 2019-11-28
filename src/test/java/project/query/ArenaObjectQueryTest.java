package project.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.EnumSet;
import java.util.LinkedList;

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

    @Test
    public void testConstructor() {
        DummySelector s1 = new DummySelector(1);
        DummySelector s2 = new DummySelector(2);
        DummySelector s3 = new DummySelector(3);

        ArenaObjectQuery q1 = new ArenaObjectQuery();
        assertTrue(q1.selectors.isEmpty());

        ArenaObjectQuery q2 = new ArenaObjectQuery(s1);
        assertEquals(q2.selectors.size(), 1);

        LinkedList<ArenaObjectSelector> l = new LinkedList<>(); l.add(s1); l.add(s3); l.add(s2);
        ArenaObjectQuery q3 = new ArenaObjectQuery(l);
        assertEquals(q3.selectors.size(), 3);;
    }

    @Test
    public void testQuerySequence() {
        ArenaObjectStorage storage = ArenaManager.getActiveObjectStorage();

        DummySelector s1 = new DummySelector(1);
        DummySelector s2 = new DummySelector(2);
        DummySelector s3 = new DummySelector(3);

        ArenaObjectQuery q = new ArenaObjectQuery();
        q.restrict(s1); assertEquals(q.selectors.size(), 1);
        q.restrict(s3); assertEquals(q.selectors.size(), 2);
        q.restrict(s1); assertEquals(q.selectors.size(), 2); // No duplicates
        q.restrict(s2); assertEquals(q.selectors.size(), 3);
        q.run(storage, EnumSet.noneOf(StoredType.class));
        assertEquals(selectedSelector, s1);
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
            LinkedList<ArenaObject> result = q.run(storage, EnumSet.noneOf(StoredType.class));
            assertTrue(result.isEmpty());
        }
        {
            LinkedList<ArenaObject> result = q.run(storage, EnumSet.of(StoredType.MONSTER));
            LinkedList<ArenaObject> actual = new LinkedList<>(); actual.add(m);
            assertTrue(CollectionComparator.isElementSetEqual(result, actual));
        }
        {
            LinkedList<ArenaObject> result = q.run(storage, EnumSet.of(StoredType.TOWER));
            LinkedList<ArenaObject> actual = new LinkedList<>(); actual.add(t1); actual.add(t2);
            assertTrue(CollectionComparator.isElementSetEqual(result, actual));
        }
        {
            LinkedList<ArenaObject> result = q.run(storage, EnumSet.of(StoredType.PROJECTILE));
            LinkedList<ArenaObject> actual = new LinkedList<>(); actual.add(p);
            assertTrue(CollectionComparator.isElementSetEqual(result, actual));
        }
    }
}