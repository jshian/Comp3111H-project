package project.query;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

import project.JavaFXTester;
import project.control.ArenaManager;
import project.entity.ArenaObject;
import project.entity.Monster;
import project.entity.Projectile;
import project.entity.Tower;
import project.query.ArenaObjectStorage.StoredType;
import project.util.CollectionComparator;

/**
 * Interface for tests of classes that implement {@link ArenaObjectSelector}.
 */
public abstract class ArenaObjectSelectorTest extends JavaFXTester {
    // The objects to be tested
    protected List<ArenaObjectSelector> selectors = new ArrayList<>(NUM_RANDOM_TEST_CASES);

    protected List<List<Tower>> expectedTowers = new ArrayList<>(NUM_RANDOM_TEST_CASES);
    protected List<List<Monster>> expectedMonsters = new ArrayList<>(NUM_RANDOM_TEST_CASES);
    protected List<List<Projectile>> expectedProjectiles = new ArrayList<>(NUM_RANDOM_TEST_CASES);
    {
        for (int i = 0; i < NUM_RANDOM_TEST_CASES; i++) {
            expectedTowers.add(new LinkedList<>());
            expectedMonsters.add(new LinkedList<>());
            expectedProjectiles.add(new LinkedList<>());
        }
    }

    // Number of random test cases
    protected static int NUM_RANDOM_TEST_CASES = 40;
    protected static int NUM_POPULATE_SETS = 1000;
    protected List<String> objectInfo = new ArrayList<>(NUM_RANDOM_TEST_CASES);

    /**
     * Constructs a newly allocated {@link ArenaObjectSelectorTest} object.
     */
    public ArenaObjectSelectorTest() {
        List<Object[]> argSets = generateArgSets();

        for (int i = 0; i < NUM_RANDOM_TEST_CASES; i++) {
            selectors.add(createObject(argSets.get(i)));
            objectInfo.add(createObjectInfo(argSets.get(i)));
        }
    }

    /**
     * Generates the argument sets for the test {@link ArenaObjectSelector}s.
     */
    protected abstract List<Object[]> generateArgSets();

    /**
     * Creates an {@link ArenaObjectSelector}.
     * @param args The arguments to pass to the constructor.
     */
    protected abstract ArenaObjectSelector createObject(Object... args);

    /**
     * Creates {@link #objectInfo} for an {@link ArenaObjectSelector}.
     * @param args The arguments passed to the constructor of that object.
     */
    protected abstract String createObjectInfo(Object... args);

    /**
     * Populates the {@link ArenaObjectStorage}.
     */
    protected void populate() {
        ArenaObjectStorageHelper.disableScalarFieldUpdates();

        for (int n = 0; n < NUM_POPULATE_SETS; n++) {
            Tower t = ArenaObjectStorageHelper.addTower(this);
            for (int i = 0; i < NUM_RANDOM_TEST_CASES; i++) {
                if (selectors.get(i).isInSelectionByDefinition(t)) {
                    expectedTowers.get(i).add(t);
                }
            }

            Monster m = ArenaObjectStorageHelper.addMonster(this);
            for (int i = 0; i < NUM_RANDOM_TEST_CASES; i++) {
                if (selectors.get(i).isInSelectionByDefinition(m)) {
                    expectedMonsters.get(i).add(m);
                }
            }

            Projectile p = ArenaObjectStorageHelper.addProjectile(this, t, m);
            for (int i = 0; i < NUM_RANDOM_TEST_CASES; i++) {
                if (selectors.get(i).isInSelectionByDefinition(p)) {
                    expectedProjectiles.get(i).add(p);
                }
            }
        }
    }

    public void test() {
        populate();

        ArenaObjectStorage storage = ArenaManager.getActiveObjectStorage();

        for (int i = 0; i < NUM_RANDOM_TEST_CASES; i++) {
            System.out.println("Testing valid ArenaObjectSelector with " + objectInfo);

            assertTrue(CollectionComparator.isElementSetEqual(new LinkedList<ArenaObject>(expectedTowers.get(i)),
                    storage.getQueryResult(selectors.get(i), EnumSet.of(StoredType.TOWER))));
            assertTrue(CollectionComparator.isElementSetEqual(new LinkedList<ArenaObject>(expectedMonsters.get(i)),
                    storage.getQueryResult(selectors.get(i), EnumSet.of(StoredType.MONSTER))));
            assertTrue(CollectionComparator.isElementSetEqual(new LinkedList<ArenaObject>(expectedProjectiles.get(i)),
                    storage.getQueryResult(selectors.get(i), EnumSet.of(StoredType.PROJECTILE))));
        }
    }
}