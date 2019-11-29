package project.query;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import project.JavaFXTester;
import project.control.ArenaManager;
import project.entity.Monster;
import project.entity.Projectile;
import project.entity.Tower;
import project.query.ArenaObjectStorage.SortOption;
import project.query.ArenaObjectStorage.StoredComparableType;
import project.util.CollectionComparator;

/**
 * Tests the {@link ArenaObjectRectangleSelector} class.
 */
@RunWith(Parameterized.class)
public class ArenaObjectRectangleSortedSelectorTest extends JavaFXTester {
    // The parameters to inject
    private short leftX;
    private short topY;
    private short width;
    private short height;

    // The objects to be tested
    private ArenaObjectRectangleSortedSelector<Monster> rectangleSortedSelector;
    private List<Monster> expectedMonsters = new LinkedList<>();

    // Number of random test cases
    private static int NUM_RANDOM_TEST_CASES = 40;
    private static int NUM_RANDOM_OBJECT_SETS = 100;
    private String objectInfo;

    @Before
    public void createObject() {
        rectangleSortedSelector = new ArenaObjectRectangleSortedSelector<Monster>(leftX, topY, width, height);
    }

    /**
     * Runs a test case for an {@link ArenaObjectRectangleSortedSelector}.
     * @param leftX The minimum x-coordinate of the rectangle.
     * @param topY The minimum y-coordinate of the rectangle.
     * @param width The x-length of the rectangle, must be non-negative.
     * @param height The y-length of the rectangle, must be non-negative.
     */
    public ArenaObjectRectangleSortedSelectorTest(short leftX, short topY, short width, short height) {
        this.leftX = leftX;
        this.topY = topY;
        this.width = width;
        this.height = height;
        this.objectInfo = String.format("leftX = %d, topY = %d, width = %d, height = %d", leftX, topY, width, height);

        System.out.println("Testing valid ArenaObjectRectangleSortedSelector<Monster> with " + objectInfo);
    }

    @Parameters
    public static Collection<Object[]> getParameters() {
        Object[][] randomParams = new Object[NUM_RANDOM_TEST_CASES][];

        Random rng = new Random();
        for (int i = 0; i < NUM_RANDOM_TEST_CASES; i++) {
            randomParams[i] = new Object[] {
                (short) rng.nextInt(ArenaManager.ARENA_WIDTH + 1),
                (short) rng.nextInt(ArenaManager.ARENA_HEIGHT + 1),
                (short) rng.nextInt(ArenaManager.ARENA_WIDTH + 1),
                (short) rng.nextInt(ArenaManager.ARENA_HEIGHT + 1)
            };
        }

        LinkedList<Object[]> boundaryParams = new LinkedList<>();
        boundaryParams.add(new Object[] { ZERO, ZERO, ZERO, ZERO });
        boundaryParams.add(new Object[] { ArenaManager.ARENA_WIDTH, ZERO, ZERO, ZERO });
        boundaryParams.add(new Object[] { ZERO, ArenaManager.ARENA_HEIGHT, ZERO, ZERO });
        boundaryParams.add(new Object[] { ArenaManager.ARENA_WIDTH, ArenaManager.ARENA_HEIGHT, ZERO, ZERO });
        boundaryParams.add(new Object[] { ZERO, (short) rng.nextInt(ArenaManager.ARENA_HEIGHT), ZERO, ZERO });
        boundaryParams.add(new Object[] { (short) rng.nextInt(ArenaManager.ARENA_WIDTH), ZERO, ZERO, ZERO });
        boundaryParams.add(new Object[] { ArenaManager.ARENA_WIDTH, (short) rng.nextInt(ArenaManager.ARENA_HEIGHT), ZERO, ZERO });
        boundaryParams.add(new Object[] { (short) rng.nextInt(ArenaManager.ARENA_WIDTH), ArenaManager.ARENA_HEIGHT, ZERO, ZERO });
        boundaryParams.add(new Object[] { ZERO, ZERO, ArenaManager.ARENA_WIDTH, ZERO });
        boundaryParams.add(new Object[] { ArenaManager.ARENA_WIDTH, ZERO, ArenaManager.ARENA_WIDTH, ZERO });
        boundaryParams.add(new Object[] { ZERO, ArenaManager.ARENA_HEIGHT, ArenaManager.ARENA_WIDTH, ZERO });
        boundaryParams.add(new Object[] { ArenaManager.ARENA_WIDTH, ArenaManager.ARENA_HEIGHT, ArenaManager.ARENA_WIDTH, ZERO });
        boundaryParams.add(new Object[] { ZERO, (short) rng.nextInt(ArenaManager.ARENA_HEIGHT), ArenaManager.ARENA_WIDTH, ZERO });
        boundaryParams.add(new Object[] { (short) rng.nextInt(ArenaManager.ARENA_WIDTH), ZERO, ArenaManager.ARENA_WIDTH, ZERO });
        boundaryParams.add(new Object[] { ArenaManager.ARENA_WIDTH, (short) rng.nextInt(ArenaManager.ARENA_HEIGHT), ArenaManager.ARENA_WIDTH, ZERO });
        boundaryParams.add(new Object[] { (short) rng.nextInt(ArenaManager.ARENA_WIDTH), ArenaManager.ARENA_HEIGHT, ArenaManager.ARENA_WIDTH, ZERO });
        boundaryParams.add(new Object[] { ZERO, ZERO, ZERO, ArenaManager.ARENA_HEIGHT });
        boundaryParams.add(new Object[] { ArenaManager.ARENA_WIDTH, ZERO, ZERO, ArenaManager.ARENA_HEIGHT });
        boundaryParams.add(new Object[] { ZERO, ArenaManager.ARENA_HEIGHT, ZERO, ArenaManager.ARENA_HEIGHT });
        boundaryParams.add(new Object[] { ArenaManager.ARENA_WIDTH, ArenaManager.ARENA_HEIGHT, ZERO, ArenaManager.ARENA_HEIGHT });
        boundaryParams.add(new Object[] { ZERO, (short) rng.nextInt(ArenaManager.ARENA_HEIGHT), ZERO, ArenaManager.ARENA_HEIGHT });
        boundaryParams.add(new Object[] { (short) rng.nextInt(ArenaManager.ARENA_WIDTH), ZERO, ZERO, ArenaManager.ARENA_HEIGHT });
        boundaryParams.add(new Object[] { ArenaManager.ARENA_WIDTH, (short) rng.nextInt(ArenaManager.ARENA_HEIGHT), ZERO, ArenaManager.ARENA_HEIGHT });
        boundaryParams.add(new Object[] { (short) rng.nextInt(ArenaManager.ARENA_WIDTH), ArenaManager.ARENA_HEIGHT, ZERO, ArenaManager.ARENA_HEIGHT });
        boundaryParams.add(new Object[] { ZERO, ZERO, ArenaManager.ARENA_WIDTH, ArenaManager.ARENA_HEIGHT });
        boundaryParams.add(new Object[] { ArenaManager.ARENA_WIDTH, ZERO, ArenaManager.ARENA_WIDTH, ArenaManager.ARENA_HEIGHT });
        boundaryParams.add(new Object[] { ZERO, ArenaManager.ARENA_HEIGHT, ArenaManager.ARENA_WIDTH, ArenaManager.ARENA_HEIGHT });
        boundaryParams.add(new Object[] { ArenaManager.ARENA_WIDTH, ArenaManager.ARENA_HEIGHT, ArenaManager.ARENA_WIDTH, ArenaManager.ARENA_HEIGHT });
        boundaryParams.add(new Object[] { ZERO, (short) rng.nextInt(ArenaManager.ARENA_HEIGHT), ArenaManager.ARENA_WIDTH, ArenaManager.ARENA_HEIGHT });
        boundaryParams.add(new Object[] { (short) rng.nextInt(ArenaManager.ARENA_WIDTH), ZERO, ArenaManager.ARENA_WIDTH, ArenaManager.ARENA_HEIGHT });
        boundaryParams.add(new Object[] { ArenaManager.ARENA_WIDTH, (short) rng.nextInt(ArenaManager.ARENA_HEIGHT), ArenaManager.ARENA_WIDTH, ArenaManager.ARENA_HEIGHT });
        boundaryParams.add(new Object[] { (short) rng.nextInt(ArenaManager.ARENA_WIDTH), ArenaManager.ARENA_HEIGHT, ArenaManager.ARENA_WIDTH, ArenaManager.ARENA_HEIGHT });

        LinkedList<Object[]> totalParams = new LinkedList<>(Arrays.asList(randomParams));
        totalParams.addAll(boundaryParams);

        return totalParams;
    }

    @Test
    public void test() {
        ArenaObjectStorageHelper.disableScalarFieldUpdates();

        List<Monster> monsters = new LinkedList<>();
        for (int n = 0; n < NUM_RANDOM_OBJECT_SETS; n++) {
            Tower t = ArenaObjectStorageHelper.addTower(this);
            
            Monster m = ArenaObjectStorageHelper.addMonster(this);
            monsters.add(m);
            if (rectangleSortedSelector.isInSelectionByDefinition(m)) expectedMonsters.add(m);

            Projectile p = ArenaObjectStorageHelper.addProjectile(this, t, m);
        }

        ArenaObjectStorage storage = ArenaManager.getActiveObjectStorage();

        List<Monster> expectedMonsterAsc = new LinkedList<>(monsters);
        expectedMonsterAsc.sort(null);
        assertTrue(CollectionComparator.isElementSetAndOrderEqual(expectedMonsterAsc,
                storage.getSortedQueryResult(rectangleSortedSelector, StoredComparableType.MONSTER, SortOption.ASCENDING)));

        List<Monster> expectedMonsterDesc = new LinkedList<>(monsters);
        expectedMonsterAsc.sort((o1, o2) -> o2.compareTo(o1));
        assertTrue(CollectionComparator.isElementSetAndOrderEqual(expectedMonsterDesc,
                storage.getSortedQueryResult(rectangleSortedSelector, StoredComparableType.MONSTER, SortOption.DESCENDING)));
    }
}