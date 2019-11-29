package project.query;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import project.JavaFXTester;
import project.control.ArenaManager;
import project.entity.Monster;
import project.entity.Projectile;
import project.entity.Tower;
import project.query.ArenaObjectStorage.SortOption;
import project.query.ArenaObjectStorage.StoredComparableType;
import project.util.CollectionComparator;

/**
 * Tests a combination of classes that implement {@link ArenaObjectSortedSelector}, sharing the same pool of objects.
 */
public class ArenaObjectSortedSelectorCombinedTest extends JavaFXTester {
    // Tests
    protected ArenaObjectCircleSortedSelectorGenerator circleSortedSelectorTest = new ArenaObjectCircleSortedSelectorGenerator();
    protected ArenaObjectGridSortedSelectorGenerator gridSortedSelectorTest = new ArenaObjectGridSortedSelectorGenerator();
    protected ArenaObjectRectangleSortedSelectorGenerator rectangleSortedSelectorTest = new ArenaObjectRectangleSortedSelectorGenerator();
    protected ArenaObjectRingSortedSelectorGenerator ringSortedSelectorTest = new ArenaObjectRingSortedSelectorGenerator();

    protected List<ArenaObjectSortedSelectorGenerator> tests = new ArrayList<>();
    {
        tests.add(circleSortedSelectorTest);
        tests.add(gridSortedSelectorTest);
        tests.add(rectangleSortedSelectorTest);
        tests.add(ringSortedSelectorTest);
    }

    // The objects to be tested
    protected List<List<ArenaObjectSortedSelector<Monster>>> sortedSelectors = new ArrayList<>();
    {
        for (int k = 0; k < tests.size(); k++) {
            sortedSelectors.add(new ArrayList<>(ArenaObjectSortedSelectorGenerator.NUM_SELECTORS));
        }
    }

    protected List<List<List<Monster>>> expectedMonsters = new ArrayList<>();
    {
        for (int k = 0; k < tests.size(); k++) {
            expectedMonsters.add(new ArrayList<>(ArenaObjectSortedSelectorGenerator.NUM_SELECTORS));

            for (int i = 0; i < ArenaObjectSortedSelectorGenerator.NUM_SELECTORS; i++) {
                expectedMonsters.get(k).add(new LinkedList<>());
            }
        }
    }

    protected List<List<String>> objectInfo = new ArrayList<>();
    {
        for (int k = 0; k < tests.size(); k++) {
            objectInfo.add(new ArrayList<>(ArenaObjectSortedSelectorGenerator.NUM_SELECTORS));
        }
    }

    /**
     * Constructs a newly allocated {@link ArenaObjectSortedSelectorCombinedTest} object.
     */
    public ArenaObjectSortedSelectorCombinedTest() {
        for (int k = 0; k < tests.size(); k++) {
            List<Object[]> argSets = tests.get(k).generateArgSets();

            for (int i = 0; i < ArenaObjectSortedSelectorGenerator.NUM_SELECTORS; i++) {
                sortedSelectors.get(k).add(tests.get(k).generateSortedSelector(argSets.get(i)));
                objectInfo.get(k).add(tests.get(k).generateSelectorInfo(argSets.get(i)));
            }
        }
    }

    /**
     * Number of object sets to populate the arena with.
     */
    static int NUM_POPULATE_SETS = 1000;

    /**
     * Populates the {@link ArenaObjectStorage}.
     */
    protected void populate() {
        ArenaObjectStorageHelper.disableScalarFieldUpdates();

        for (int n = 0; n < NUM_POPULATE_SETS; n++) {
            Tower t = ArenaObjectStorageHelper.addTower(this);

            Monster m = ArenaObjectStorageHelper.addMonster(this);
            for (int k = 0; k < tests.size(); k++) {
                for (int i = 0; i < ArenaObjectSortedSelectorGenerator.NUM_SELECTORS; i++) {
                    if (sortedSelectors.get(k).get(i).isInSelectionByDefinition(m)) {
                        expectedMonsters.get(k).get(i).add(m);
                    }
                }
            }

            Projectile p = ArenaObjectStorageHelper.addProjectile(this, t, m);
        }
    }

    @Test
    public void test() {
        populate();

        ArenaObjectStorage storage = ArenaManager.getActiveObjectStorage();

        for (int k = 0; k < tests.size(); k++) {
            for (int i = 0; i < ArenaObjectSortedSelectorGenerator.NUM_SELECTORS; i++) {
                System.out.println(String.format("Testing valid %s with ", tests.get(k).getClass().getSimpleName()) + objectInfo.get(k).get(i));

                List<Monster> expectedMonsterAsc = new LinkedList<>(expectedMonsters.get(k).get(i));
                expectedMonsterAsc.sort(null);
                assertTrue(CollectionComparator.isElementSetAndOrderEqual(expectedMonsterAsc,
                        storage.getSortedQueryResult(sortedSelectors.get(k).get(i), StoredComparableType.MONSTER, SortOption.ASCENDING)));
        
                List<Monster> expectedMonsterDesc = new LinkedList<>(expectedMonsters.get(k).get(i));
                expectedMonsterDesc.sort((o1, o2) -> o2.compareTo(o1));
                assertTrue(CollectionComparator.isElementSetAndOrderEqual(expectedMonsterDesc,
                        storage.getSortedQueryResult(sortedSelectors.get(k).get(i), StoredComparableType.MONSTER, SortOption.DESCENDING)));
            }
        }
    }
}