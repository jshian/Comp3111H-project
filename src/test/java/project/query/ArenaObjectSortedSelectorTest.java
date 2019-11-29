package project.query;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import project.JavaFXTester;
import project.control.ArenaManager;
import project.entity.Monster;
import project.entity.Projectile;
import project.entity.Tower;
import project.query.ArenaObjectStorage.SortOption;
import project.query.ArenaObjectStorage.StoredComparableType;
import project.util.CollectionComparator;

/**
 * Interface for tests of classes that implement {@link ArenaObjectSortedSelector} using {@link Monster}s.
 */
public abstract class ArenaObjectSortedSelectorTest extends JavaFXTester {
    // The objects to be tested
    protected List<ArenaObjectSortedSelector<Monster>> sortedSelectors = new ArrayList<>(NUM_RANDOM_TEST_CASES);

    protected List<List<Monster>> expectedMonsters = new ArrayList<>(NUM_RANDOM_TEST_CASES);
    {
        for (int i = 0; i < NUM_RANDOM_TEST_CASES; i++) {
            expectedMonsters.add(new LinkedList<>());
        }
    }

    // Number of random test cases
    protected static int NUM_RANDOM_TEST_CASES = 40;
    protected static int NUM_POPULATE_SETS = 1000;
    protected List<String> objectInfo = new ArrayList<>(NUM_RANDOM_TEST_CASES);

    /**
     * Constructs a newly allocated {@link ArenaObjectSortedSelectorTest} object.
     */
    public ArenaObjectSortedSelectorTest() {
        List<Object[]> argSets = generateArgSets();

        for (int i = 0; i < NUM_RANDOM_TEST_CASES; i++) {
            sortedSelectors.add(createObject(argSets.get(i)));
            objectInfo.add(createObjectInfo(argSets.get(i)));
        }
    }

    /**
     * Generates the argument sets for the test {@link ArenaObjectSortedSelector}s.
     */
    protected abstract List<Object[]> generateArgSets();

    /**
     * Creates an {@link ArenaObjectSelector} and sets up {@link #objectInfo} for that object.
     * @param args The arguments to pass to the constructor.
     */
    protected abstract ArenaObjectSortedSelector<Monster> createObject(Object... args);

    /**
     * Creates {@link #objectInfo} for an {@link ArenaObjectSortedSelector}.
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

            Monster m = ArenaObjectStorageHelper.addMonster(this);
            for (int i = 0; i < NUM_RANDOM_TEST_CASES; i++) {
                if (sortedSelectors.get(i).isInSelectionByDefinition(m)) {
                    expectedMonsters.get(i).add(m);
                }
            }

            Projectile p = ArenaObjectStorageHelper.addProjectile(this, t, m);
        }
    }

    public void test() {
        populate();
        ArenaObjectStorage storage = ArenaManager.getActiveObjectStorage();

        for (int i = 0; i < NUM_RANDOM_TEST_CASES; i++) {
            System.out.println("Testing valid ArenaObjectSelector with " + objectInfo);

            List<Monster> expectedMonsterAsc = new LinkedList<>(expectedMonsters.get(i));
            expectedMonsterAsc.sort(null);
            assertTrue(CollectionComparator.isElementSetAndOrderEqual(expectedMonsterAsc,
                    storage.getSortedQueryResult(sortedSelectors.get(i), StoredComparableType.MONSTER, SortOption.ASCENDING)));
    
            List<Monster> expectedMonsterDesc = new LinkedList<>(expectedMonsters.get(i));
            expectedMonsterDesc.sort((o1, o2) -> o2.compareTo(o1));
            assertTrue(CollectionComparator.isElementSetAndOrderEqual(expectedMonsterDesc,
                    storage.getSortedQueryResult(sortedSelectors.get(i), StoredComparableType.MONSTER, SortOption.DESCENDING)));
        }
    }
}