package project.arena;

import static org.junit.Assert.fail;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Tests the {@link Coordinates} class.
 */
public class CoordinatesTester {
	@Rule
	public final ExpectedException expectedException = ExpectedException.none();
	
	public void assertInvalidParameterException_constructorCoordinates(short x, short y) {
		try {
			Coordinates c = new Coordinates(x, y);
			fail(String.format("The constructor with parameters (%d, %d) should have thrown an exception.", x, y));
		} catch (IllegalArgumentException e) {
			
		}
	}

	@Test
	public void testConstructor() {
		// Boundary Cases
		Coordinates c_b1 = new Coordinates((short) 0, (short) 0);
		Coordinates c_b2 = new Coordinates((short) 0, (short) 480);
		Coordinates c_b3 = new Coordinates((short) 480, (short) 0);
		Coordinates c_b4 = new Coordinates((short) 480, (short) 480);
		assertInvalidParameterException_constructorCoordinates((short) -1, (short) 0);
		assertInvalidParameterException_constructorCoordinates((short) 0, (short) -1);
		assertInvalidParameterException_constructorCoordinates((short) 481, (short) 0);
		assertInvalidParameterException_constructorCoordinates((short) 0, (short) 481);

		// Typical Cases
		Coordinates c_t1 = new Coordinates((short) 123, (short) 407);
		Coordinates c_t2 = new Coordinates((short) 274, (short) 190);
		Coordinates c_t3 = new Coordinates((short) 6, (short) 256);
		Coordinates c_t4 = new Coordinates((short) 445, (short) 77);
		assertInvalidParameterException_constructorCoordinates((short) -215, (short) 0);
		assertInvalidParameterException_constructorCoordinates((short) 0, (short) -159);
		assertInvalidParameterException_constructorCoordinates((short) 1203, (short) 0);
		assertInvalidParameterException_constructorCoordinates((short) 0, (short) 800);
	}
} 