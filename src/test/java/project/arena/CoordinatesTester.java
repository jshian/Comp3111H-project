package project.arena;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class CoordinatesTester {
	@Rule
	public final ExpectedException expectedException = ExpectedException.none();
	
	@Test
	public void testCoordinateConstructor() {
		// Boundary Cases
		Coordinates c_b1 = new Coordinates((short) 0, (short) 0);
		Coordinates c_b2 = new Coordinates((short) 0, (short) 480);
		Coordinates c_b3 = new Coordinates((short) 480, (short) 0);
		Coordinates c_b4 = new Coordinates((short) 480, (short) 480);
		expectedException.expect(IllegalArgumentException.class); Coordinates c_b5 = new Coordinates((short) -1, (short) 0);
		expectedException.expect(IllegalArgumentException.class); Coordinates c_b6 = new Coordinates((short) 0, (short) -1);
		expectedException.expect(IllegalArgumentException.class); Coordinates c_b7 = new Coordinates((short) 481, (short) 0);
		expectedException.expect(IllegalArgumentException.class); Coordinates c_b8 = new Coordinates((short) 0, (short) 481);

		// Typical Cases
		Coordinates c_t1 = new Coordinates((short) 123, (short) 407);
		Coordinates c_t2 = new Coordinates((short) 274, (short) 190);
		Coordinates c_t3 = new Coordinates((short) 6, (short) 256);
		Coordinates c_t4 = new Coordinates((short) 445, (short) 77);
		expectedException.expect(IllegalArgumentException.class); Coordinates c_t5 = new Coordinates((short) -215, (short) 0);
		expectedException.expect(IllegalArgumentException.class); Coordinates c_t6 = new Coordinates((short) 0, (short) -159);
		expectedException.expect(IllegalArgumentException.class); Coordinates c_t7 = new Coordinates((short) 1203, (short) 0);
		expectedException.expect(IllegalArgumentException.class); Coordinates c_t8 = new Coordinates((short) 0, (short) 800);
	}
} 