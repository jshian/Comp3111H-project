package project.entity;

import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import project.JavaFXTester;
import project.control.ArenaManager;

/**
 * Tests the {@link ArenaObject} base class for invalid parameters.
 */
@RunWith(Parameterized.class)
public class InvalidArenaObjectTest extends JavaFXTester {
    // The parameters to inject
    private short x;
    private short y;
    
    // Number of random test cases
    private static int NUM_RANDOM_TEST_CASES = 5;
    private String objectInfo;

    /**
     * Runs a test case for an invalid {@link ArenaObject}.
     * @param x The valid x-coordinate of the object. Ignored when set to invalid.
     * @param y The valid y-coordinate of the object. Ignored when set to invalid.
     */
    public InvalidArenaObjectTest(short x, short y) {
        this.x = x;
        this.y = y;
        this.objectInfo = String.format("x = %d, y = %d", x, y);
        
        System.out.println("Testing invalid ArenaObject with valid parameters " + objectInfo);
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

        return Arrays.asList(randomParams);
    }


    @Test
    public void testConstructorNegativePositionX() {
        Random rng = new Random();
        short invalidX = (short) (-1 * rng.nextInt(ArenaManager.ARENA_WIDTH + 1));

        expectedException.expect(IllegalArgumentException.class);

        System.out.println(String.format("Test invalidX = %s", invalidX));
        new ArenaObject(invalidX, y) {
            @Override
            protected ImageView getDefaultImage() {
                return new ImageView(new Image("/empty.png", 1, 1, true, true));
            }
        };
    }

    @Test
    public void testConstructorNegativePositionY() {
        Random rng = new Random();
        short invalidY = (short) (-1 * rng.nextInt(ArenaManager.ARENA_HEIGHT + 1));

        expectedException.expect(IllegalArgumentException.class);

        System.out.println(String.format("Test invalidY = %s", invalidY));
        new ArenaObject(x, invalidY) {
            @Override
            protected ImageView getDefaultImage() {
                return new ImageView(new Image("/empty.png", 1, 1, true, true));
            }
        };
    }

    @Test
    public void testConstructorOutofBoundsPositionX() {
        Random rng = new Random();
        short invalidX = (short) (ArenaManager.ARENA_WIDTH + 1 + rng.nextInt(ArenaManager.ARENA_WIDTH + 1));

        expectedException.expect(IllegalArgumentException.class);

        System.out.println(String.format("Test invalidX = %s", invalidX));
        new ArenaObject(invalidX, y) {
            @Override
            protected ImageView getDefaultImage() {
                return new ImageView(new Image("/empty.png", 1, 1, true, true));
            }
        };
    }

    @Test
    public void testConstructorOutofBoundsPositionY() {

        Random rng = new Random();
        short invalidY = (short) (ArenaManager.ARENA_HEIGHT + 1 + rng.nextInt(ArenaManager.ARENA_HEIGHT + 1));

        expectedException.expect(IllegalArgumentException.class);

        System.out.println(String.format("Test invalidY = %s", invalidY));
        new ArenaObject(x, invalidY) {
            @Override
            protected ImageView getDefaultImage() {
                return new ImageView(new Image("/empty.png", 1, 1, true, true));
            }
        };
    }
}