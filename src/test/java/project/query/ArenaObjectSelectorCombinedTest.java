package project.query;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import project.JavaFXTester;
import project.control.ArenaManager;
import project.entity.ArenaObject;
import project.entity.Monster;
import project.entity.Projectile;
import project.entity.Tower;
import project.query.ArenaObjectStorage.StoredType;
import project.util.CollectionComparator;

/**
 * Tests a combination of classes that implement {@link ArenaObjectSelector}, sharing the same pool of objects.
 */
public class ArenaObjectSelectorCombinedTest extends JavaFXTester {
    // Tests
    protected ArenaObjectCircleSelectorGenerator circleSelectorTest = new ArenaObjectCircleSelectorGenerator();
    protected ArenaObjectGridSelectorGenerator gridSelectorTest = new ArenaObjectGridSelectorGenerator();
    protected ArenaObjectRectangleSelectorGenerator rectangleSelectorTest = new ArenaObjectRectangleSelectorGenerator();
    protected ArenaObjectRingSelectorGenerator ringSelectorTest = new ArenaObjectRingSelectorGenerator();

    protected List<ArenaObjectSelectorGenerator> tests = new ArrayList<>();
    {
        tests.add(circleSelectorTest);
        tests.add(gridSelectorTest);
        tests.add(rectangleSelectorTest);
        tests.add(ringSelectorTest);
    }

    // The objects to be tested
    protected List<List<ArenaObjectSelector>> selectors = new ArrayList<>();
    {
        for (int k = 0; k < tests.size(); k++) {
            selectors.add(new ArrayList<>(ArenaObjectSelectorGenerator.NUM_SELECTORS));
        }
    }

    protected List<List<List<Tower>>> expectedTowers = new ArrayList<>();
    protected List<List<List<Monster>>> expectedMonsters = new ArrayList<>();
    protected List<List<List<Projectile>>> expectedProjectiles = new ArrayList<>();
    {
        for (int k = 0; k < tests.size(); k++) {
            expectedTowers.add(new ArrayList<>(ArenaObjectSelectorGenerator.NUM_SELECTORS));
            expectedMonsters.add(new ArrayList<>(ArenaObjectSelectorGenerator.NUM_SELECTORS));
            expectedProjectiles.add(new ArrayList<>(ArenaObjectSelectorGenerator.NUM_SELECTORS));

            for (int i = 0; i < ArenaObjectSelectorGenerator.NUM_SELECTORS; i++) {
                expectedTowers.get(k).add(new LinkedList<>());
                expectedMonsters.get(k).add(new LinkedList<>());
                expectedProjectiles.get(k).add(new LinkedList<>());
            }
        }
    }
    protected List<List<String>> objectInfo = new ArrayList<>();
    {
        for (int k = 0; k < tests.size(); k++) {
            objectInfo.add(new ArrayList<>(ArenaObjectSelectorGenerator.NUM_SELECTORS));
        }
    }

    /**
     * Constructs a newly allocated {@link ArenaObjectSelectorCombinedTest} object.
     */
    public ArenaObjectSelectorCombinedTest() {
        for (int k = 0; k < tests.size(); k++) {
            List<Object[]> argSets = tests.get(k).generateArgSets();

            for (int i = 0; i < ArenaObjectSelectorGenerator.NUM_SELECTORS; i++) {
                selectors.get(k).add(tests.get(k).generateSortedSelector(argSets.get(i)));
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
            for (int k = 0; k < tests.size(); k++) {
                for (int i = 0; i < ArenaObjectSelectorGenerator.NUM_SELECTORS; i++) {
                    if (selectors.get(k).get(i).isInSelectionByDefinition(t)) {
                        expectedTowers.get(k).get(i).add(t);
                    }
                }
            }

            Monster m = ArenaObjectStorageHelper.addMonster(this);
            for (int k = 0; k < tests.size(); k++) {
                for (int i = 0; i < ArenaObjectSelectorGenerator.NUM_SELECTORS; i++) {
                    if (selectors.get(k).get(i).isInSelectionByDefinition(m)) {
                        expectedMonsters.get(k).get(i).add(m);
                    }
                }
            }

            Projectile p = ArenaObjectStorageHelper.addProjectile(this, t, m);
            for (int k = 0; k < tests.size(); k++) {
                for (int i = 0; i < ArenaObjectSelectorGenerator.NUM_SELECTORS; i++) {
                    if (selectors.get(k).get(i).isInSelectionByDefinition(p)) {
                        expectedProjectiles.get(k).get(i).add(p);
                    }
                }
            }
        }
    }

    @Test
    public void test() {
        populate();

        ArenaObjectStorage storage = ArenaManager.getActiveObjectStorage();

        for (int k = 0; k < tests.size(); k++) {
            for (int i = 0; i < ArenaObjectSelectorGenerator.NUM_SELECTORS; i++) {
                System.out.println(String.format("Testing valid %s with ", tests.get(k).getClass().getSimpleName()) + objectInfo.get(k).get(i));

                assertTrue(CollectionComparator.isElementSetEqual(new LinkedList<ArenaObject>(expectedTowers.get(k).get(i)),
                        storage.getQueryResult(selectors.get(k).get(i), EnumSet.of(StoredType.TOWER))));
                assertTrue(CollectionComparator.isElementSetEqual(new LinkedList<ArenaObject>(expectedMonsters.get(k).get(i)),
                        storage.getQueryResult(selectors.get(k).get(i), EnumSet.of(StoredType.MONSTER))));
                assertTrue(CollectionComparator.isElementSetEqual(new LinkedList<ArenaObject>(expectedProjectiles.get(k).get(i)),
                        storage.getQueryResult(selectors.get(k).get(i), EnumSet.of(StoredType.PROJECTILE))));
            }
        }
    }
}