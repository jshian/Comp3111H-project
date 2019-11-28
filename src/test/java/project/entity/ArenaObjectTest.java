package project.entity;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import project.JavaFXTester;
import project.control.ArenaManager;

/**
 * Tests the {@link ArenaObject} base class.
 */
@RunWith(Parameterized.class)
public class ArenaObjectTest extends JavaFXTester {
    // The parameters to inject
    private short x;
    private short y;

    // The object to be tested
    private ArenaObject arenaObject;
    
    // Number of random test cases
    private static int NUM_RANDOM_TEST_CASES = 10;
    private String objectInfo;

    @Before
    public void createObject() {
        arenaObject = new ArenaObject(x, y) {
            @Override
            protected ImageView getDefaultImage() {
                return new ImageView(new Image("/empty.png", 1, 1, true, true));
            }
        };
    }

    /**
     * Runs a test case for an {@link ArenaObject}.
     * @param x The x-coordinate of the object.
     * @param y The y-coordinate of the object.
     */
    public ArenaObjectTest(short x, short y) {
        this.x = x;
        this.y = y;
        this.objectInfo = String.format("x = %d, y = %d", x, y);

        System.out.println("Testing valid ArenaObject with " + objectInfo);
    }

    @Parameters
    public static Collection<Object[]> getParameters() {
        Object[][] randomParams = new Object[NUM_RANDOM_TEST_CASES][];

        Random rng = new Random();
        for (int i = 0; i < NUM_RANDOM_TEST_CASES; i++) {
            randomParams[i] = new Object[] {
                (short) rng.nextInt(ArenaManager.ARENA_WIDTH + 1),
                (short) rng.nextInt(ArenaManager.ARENA_HEIGHT + 1)
            };
        }

        final short ZERO = (short) 0;
        LinkedList<Object[]> boundaryParams = new LinkedList<>();
        boundaryParams.add(new Object[] { ZERO, ZERO });
        boundaryParams.add(new Object[] { ArenaManager.ARENA_WIDTH, ZERO });
        boundaryParams.add(new Object[] { ZERO, ArenaManager.ARENA_HEIGHT });
        boundaryParams.add(new Object[] { ArenaManager.ARENA_WIDTH, ArenaManager.ARENA_HEIGHT });
        boundaryParams.add(new Object[] { ZERO, (short) rng.nextInt(ArenaManager.ARENA_HEIGHT) });
        boundaryParams.add(new Object[] { (short) rng.nextInt(ArenaManager.ARENA_WIDTH), ZERO });
        boundaryParams.add(new Object[] { ArenaManager.ARENA_WIDTH, (short) rng.nextInt(ArenaManager.ARENA_HEIGHT) });
        boundaryParams.add(new Object[] { (short) rng.nextInt(ArenaManager.ARENA_WIDTH), ArenaManager.ARENA_HEIGHT });

        LinkedList<Object[]> totalParams = new LinkedList<>(Arrays.asList(randomParams));
        totalParams.addAll(boundaryParams);

        return totalParams;
    }

    @Test
    public void test() {
        assertEquals(arenaObject.getX(), x);
        assertEquals(arenaObject.getY(), y);
    }
}