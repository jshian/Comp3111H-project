package project.entity;

import static org.junit.Assert.assertEquals;

import static project.util.ExceptionThrownTester.assertExceptionThrown_constructor;

import java.util.Random;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import project.control.ArenaManager;

/**
 * Tests the {@link Coordinates} class.
 */
public class ArenaObjectPositionInfoTester {
	@Rule
	public final ExpectedException expectedException = ExpectedException.none();

	@Test
	public void testConstructor() {
		Class<?>[] constructorArgTypes = { ImageView.class, short.class, short.class };

		ImageView iv = new ImageView(new Image("/collision.png", 1, 1, true, true));

		// Boundary Cases
		{
			ArenaObjectPositionInfo c1 = new ArenaObjectPositionInfo(iv, (short) 0, (short) 0);
			assertEquals(c1.getX(), (short) 0);
			assertEquals(c1.getY(), (short) 0);

			ArenaObjectPositionInfo c2 = new ArenaObjectPositionInfo(iv, (short) 0, ArenaManager.ARENA_HEIGHT);
			assertEquals(c2.getX(), (short) 0);
			assertEquals(c2.getY(), ArenaManager.ARENA_HEIGHT);

			ArenaObjectPositionInfo c3 = new ArenaObjectPositionInfo(iv, ArenaManager.ARENA_WIDTH, (short) 0);
			assertEquals(c3.getX(), ArenaManager.ARENA_WIDTH);
			assertEquals(c3.getY(), 0);

			ArenaObjectPositionInfo c4 = new ArenaObjectPositionInfo(iv, ArenaManager.ARENA_WIDTH, ArenaManager.ARENA_HEIGHT);
			assertEquals(c4.getX(), ArenaManager.ARENA_WIDTH);
			assertEquals(c4.getY(), ArenaManager.ARENA_HEIGHT);

			assertExceptionThrown_constructor(IllegalArgumentException.class, ArenaObjectPositionInfo.class, constructorArgTypes, new Object[] { iv, (short) -1, (short) 0 });
			assertExceptionThrown_constructor(IllegalArgumentException.class, ArenaObjectPositionInfo.class, constructorArgTypes, new Object[] { iv, (short) 0, (short) -1 });
			assertExceptionThrown_constructor(IllegalArgumentException.class, ArenaObjectPositionInfo.class, constructorArgTypes, new Object[] { iv, (short) (ArenaManager.ARENA_WIDTH + 1), (short) 0 });
			assertExceptionThrown_constructor(IllegalArgumentException.class, ArenaObjectPositionInfo.class, constructorArgTypes, new Object[] { iv, (short) 0, (short) (ArenaManager.ARENA_HEIGHT + 1) });
		}

		// Typical Cases
		{
			Random rng = new Random();
			int NUM_RANDOM_CASES = 100;

			for (int i = 0; i < NUM_RANDOM_CASES; i++) {
				short x = (short) (rng.nextDouble() * ArenaManager.ARENA_WIDTH);
				short y = (short) (rng.nextDouble() * ArenaManager.ARENA_HEIGHT);

				ArenaObjectPositionInfo c = new ArenaObjectPositionInfo(iv, x, y);
				assertEquals("x test failed for x = " + x, c.getX(), x);
				assertEquals("y test failed for y = " + x, c.getY(), y);
			}

			assertExceptionThrown_constructor(IllegalArgumentException.class, ArenaObjectPositionInfo.class, constructorArgTypes, new Object[] { iv, (short) -215, (short) 0 });
			assertExceptionThrown_constructor(IllegalArgumentException.class, ArenaObjectPositionInfo.class, constructorArgTypes, new Object[] { iv, (short) 0, (short) -159 });
			assertExceptionThrown_constructor(IllegalArgumentException.class, ArenaObjectPositionInfo.class, constructorArgTypes, new Object[] { iv, (short) (ArenaManager.ARENA_WIDTH * 2.8), (short) 0 });
			assertExceptionThrown_constructor(IllegalArgumentException.class, ArenaObjectPositionInfo.class, constructorArgTypes, new Object[] { iv, (short) 0, (short) (ArenaManager.ARENA_HEIGHT * 1.2) });
		}
	}
}