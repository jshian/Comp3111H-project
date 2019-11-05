import org.junit.*;
import org.junit.rules.ExpectedException;

import project.*;

public class CoordinatesTester {
	@Rule
	public final ExpectedException expectedException = ExpectedException.none();
	
	@Test
	public void testCoordinateConstructor() {
		// Boundary Cases
		Coordinates c_b1 = new Coordinates(0, 0);
		Coordinates c_b2 = new Coordinates(0, 479);
		Coordinates c_b3 = new Coordinates(479, 0);
		Coordinates c_b4 = new Coordinates(479, 479);
		expectedException.expect(IllegalArgumentException.class); Coordinates c_b5 = new Coordinates(-1, 0);
		expectedException.expect(IllegalArgumentException.class); Coordinates c_b6 = new Coordinates(0, -1);
		expectedException.expect(IllegalArgumentException.class); Coordinates c_b7 = new Coordinates(480, 0);
		expectedException.expect(IllegalArgumentException.class); Coordinates c_b8 = new Coordinates(0, 480);

		// Typical Cases
		Coordinates c_t1 = new Coordinates(123, 407);
		Coordinates c_t2 = new Coordinates(274, 190);
		Coordinates c_t3 = new Coordinates(6, 256);
		Coordinates c_t4 = new Coordinates(445, 77);
		expectedException.expect(IllegalArgumentException.class); Coordinates c_t5 = new Coordinates(-215, 0);
		expectedException.expect(IllegalArgumentException.class); Coordinates c_t6 = new Coordinates(0, -159);
		expectedException.expect(IllegalArgumentException.class); Coordinates c_t7 = new Coordinates(1203, 0);
		expectedException.expect(IllegalArgumentException.class); Coordinates c_t8 = new Coordinates(0, 800);
	}

	@Test
	public void testCoordinateHelperFunctions() {

	}
} 