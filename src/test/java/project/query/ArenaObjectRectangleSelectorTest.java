package project.query;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
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
import project.entity.ArenaObject;
import project.entity.Monster;
import project.entity.Projectile;
import project.entity.Tower;
import project.query.ArenaObjectStorage.StoredType;
import project.util.CollectionComparator;

/**
 * Tests the {@link ArenaObjectRectangleSelector} class.
 */
@RunWith(Parameterized.class)
public class ArenaObjectRectangleSelectorTest extends JavaFXTester {
    // The parameters to inject
    private short leftX;
    private short topY;
    private short width;
    private short height;

    // The objects to be tested
    private ArenaObjectRectangleSelector rectangleSelector;
    private List<Tower> expectedTowers = new LinkedList<>();
    private List<Monster> expectedMonsters = new LinkedList<>();
    private List<Projectile> expectedProjectiles = new LinkedList<>();

    // Number of random test cases
    private static int NUM_RANDOM_TEST_CASES = 40;
    private static int NUM_RANDOM_OBJECT_SETS = 100;
    private String objectInfo;

    @Before
    public void createObject() {
        rectangleSelector = new ArenaObjectRectangleSelector(leftX, topY, width, height);
    }

    /**
     * Runs a test case for an {@link ArenaObjectRectangleSelector}.
     * @param leftX The minimum x-coordinate of the rectangle.
     * @param topY The minimum y-coordinate of the rectangle.
     * @param width The x-length of the rectangle, must be non-negative.
     * @param height The y-length of the rectangle, must be non-negative.
     */
    public ArenaObjectRectangleSelectorTest(short leftX, short topY, short width, short height) {
        this.leftX = leftX;
        this.topY = topY;
        this.width = width;
        this.height = height;
        this.objectInfo = String.format("leftX = %d, topY = %d, width = %d, height = %d", leftX, topY, width, height);

        System.out.println("Testing valid ArenaObjectRectangleSelector with " + objectInfo);
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

        for (int n = 0; n < NUM_RANDOM_OBJECT_SETS; n++) {
            Tower t = ArenaObjectStorageHelper.addTower(this);
            if (rectangleSelector.isInSelectionByDefinition(t)) expectedTowers.add(t);

            Monster m = ArenaObjectStorageHelper.addMonster(this);
            if (rectangleSelector.isInSelectionByDefinition(m)) expectedMonsters.add(m);

            Projectile p = ArenaObjectStorageHelper.addProjectile(this, t, m);
            if (rectangleSelector.isInSelectionByDefinition(p)) expectedProjectiles.add(p);
        }

        ArenaObjectStorage storage = ArenaManager.getActiveObjectStorage();

        assertTrue(CollectionComparator.isElementSetEqual(new LinkedList<ArenaObject>(expectedTowers),
                storage.getQueryResult(rectangleSelector, EnumSet.of(StoredType.TOWER))));
        assertTrue(CollectionComparator.isElementSetEqual(new LinkedList<ArenaObject>(expectedMonsters),
                storage.getQueryResult(rectangleSelector, EnumSet.of(StoredType.MONSTER))));
        assertTrue(CollectionComparator.isElementSetEqual(new LinkedList<ArenaObject>(expectedProjectiles),
                storage.getQueryResult(rectangleSelector, EnumSet.of(StoredType.PROJECTILE))));
    }
}