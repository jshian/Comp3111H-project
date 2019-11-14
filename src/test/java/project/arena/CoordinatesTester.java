package project.arena;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static project.ExceptionThrownTester.assertExceptionThrown_constructor;

/**
 * Tests the {@link Coordinates} class.
 */
public class CoordinatesTester {
	@Rule
	public final ExpectedException expectedException = ExpectedException.none();

	@Test
	public void testConstructor() {
		Class<?>[] constructorArgTypes = { short.class, short.class };

		// Boundary Cases
		Coordinates c_b1 = new Coordinates((short) 0, (short) 0);
		Coordinates c_b2 = new Coordinates((short) 0, (short) 480);
		Coordinates c_b3 = new Coordinates((short) 480, (short) 0);
		Coordinates c_b4 = new Coordinates((short) 480, (short) 480);
		assertExceptionThrown_constructor(IllegalArgumentException.class, Coordinates.class, constructorArgTypes, new Object[] { (short) -1, (short) 0 });
		assertExceptionThrown_constructor(IllegalArgumentException.class, Coordinates.class, constructorArgTypes, new Object[] { (short) 0, (short) -1 });
		assertExceptionThrown_constructor(IllegalArgumentException.class, Coordinates.class, constructorArgTypes, new Object[] { (short) 481, (short) 0 });
		assertExceptionThrown_constructor(IllegalArgumentException.class, Coordinates.class, constructorArgTypes, new Object[] { (short) 0, (short) 481 });

		// Typical Cases
		Coordinates c_t1 = new Coordinates((short) 123, (short) 407);
		Coordinates c_t2 = new Coordinates((short) 274, (short) 190);
		Coordinates c_t3 = new Coordinates((short) 6, (short) 256);
		Coordinates c_t4 = new Coordinates((short) 445, (short) 77);
		assertExceptionThrown_constructor(IllegalArgumentException.class, Coordinates.class, constructorArgTypes, new Object[] { (short) -215, (short) 0 });
		assertExceptionThrown_constructor(IllegalArgumentException.class, Coordinates.class, constructorArgTypes, new Object[] { (short) 0, (short) -159 });
		assertExceptionThrown_constructor(IllegalArgumentException.class, Coordinates.class, constructorArgTypes, new Object[] { (short) 1203, (short) 0 });
		assertExceptionThrown_constructor(IllegalArgumentException.class, Coordinates.class, constructorArgTypes, new Object[] { (short) 0, (short) 800 });
	}
} 