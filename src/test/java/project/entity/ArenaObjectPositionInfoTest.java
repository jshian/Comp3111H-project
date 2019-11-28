package project.entity;

import static org.junit.Assert.assertEquals;

import static project.util.ExceptionThrownTester.assertExceptionThrown_constructor;

import java.util.Random;

import org.junit.Test;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import project.JavaFXTester;
import project.control.ArenaManager;

/**
 * Tests the {@link ArenaObjectPositionInfoTest} class.
 */
public class ArenaObjectPositionInfoTest extends JavaFXTester {

	@Test
	public void testConstructor() {
		Class<?>[] constructorArgTypes = { ImageView.class, short.class, short.class };

		ImageView iv = new ImageView(new Image("/collision.png", 1, 1, true, true));

		// Boundary Cases
		{
			ArenaObjectPositionInfo c1 = new ArenaObjectPositionInfo(iv, ZERO, ZERO);
			assertEquals(ZERO, c1.getX());
			assertEquals(ZERO, c1.getY());

			ArenaObjectPositionInfo c2 = new ArenaObjectPositionInfo(iv, ZERO, ArenaManager.ARENA_HEIGHT);
			assertEquals(ZERO, c2.getX());
			assertEquals(ArenaManager.ARENA_HEIGHT, c2.getY());

			ArenaObjectPositionInfo c3 = new ArenaObjectPositionInfo(iv, ArenaManager.ARENA_WIDTH, ZERO);
			assertEquals(ArenaManager.ARENA_WIDTH, c3.getX());
			assertEquals(ZERO, c3.getY());

			ArenaObjectPositionInfo c4 = new ArenaObjectPositionInfo(iv, ArenaManager.ARENA_WIDTH, ArenaManager.ARENA_HEIGHT);
			assertEquals(ArenaManager.ARENA_WIDTH, c4.getX());
			assertEquals(ArenaManager.ARENA_HEIGHT, c4.getY());

			assertExceptionThrown_constructor(IllegalArgumentException.class, ArenaObjectPositionInfo.class, constructorArgTypes, new Object[] { iv, (short) -1, ZERO });
			assertExceptionThrown_constructor(IllegalArgumentException.class, ArenaObjectPositionInfo.class, constructorArgTypes, new Object[] { iv, ZERO, (short) -1 });
			assertExceptionThrown_constructor(IllegalArgumentException.class, ArenaObjectPositionInfo.class, constructorArgTypes, new Object[] { iv, (short) (ArenaManager.ARENA_WIDTH + 1), ZERO });
			assertExceptionThrown_constructor(IllegalArgumentException.class, ArenaObjectPositionInfo.class, constructorArgTypes, new Object[] { iv, ZERO, (short) (ArenaManager.ARENA_HEIGHT + 1) });
		}

		// Typical Cases
		{
			Random rng = new Random();
			int NUM_RANDOM_CASES = 100;

			for (int i = 0; i < NUM_RANDOM_CASES; i++) {
				short x = (short) (rng.nextDouble() * ArenaManager.ARENA_WIDTH);
				short y = (short) (rng.nextDouble() * ArenaManager.ARENA_HEIGHT);

				ArenaObjectPositionInfo c = new ArenaObjectPositionInfo(iv, x, y);
				assertEquals("x test failed for x = " + x, x, c.getX());
				assertEquals("y test failed for y = " + x, y, c.getY());
			}

			assertExceptionThrown_constructor(IllegalArgumentException.class, ArenaObjectPositionInfo.class, constructorArgTypes, new Object[] { iv, (short) -215, (short) 0 });
			assertExceptionThrown_constructor(IllegalArgumentException.class, ArenaObjectPositionInfo.class, constructorArgTypes, new Object[] { iv, (short) 0, (short) -159 });
			assertExceptionThrown_constructor(IllegalArgumentException.class, ArenaObjectPositionInfo.class, constructorArgTypes, new Object[] { iv, (short) (ArenaManager.ARENA_WIDTH * 2.8), (short) 0 });
			assertExceptionThrown_constructor(IllegalArgumentException.class, ArenaObjectPositionInfo.class, constructorArgTypes, new Object[] { iv, (short) 0, (short) (ArenaManager.ARENA_HEIGHT * 1.2) });
		}
	}
}