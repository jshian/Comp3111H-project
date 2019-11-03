import java.security.InvalidParameterException;

import org.junit.*;

import project.*;

public class CoordinatesTester extends JUnitTester {
	@Test
	public void testCoordinateConstructor() {
		// Boundary Cases
		Coordinates c_b1 = new Coordinates(0, 0);
		Coordinates c_b2 = new Coordinates(0, 480);
		Coordinates c_b3 = new Coordinates(480, 0);
		Coordinates c_b4 = new Coordinates(480, 480);
		expectedException.expect(InvalidParameterException.class); Coordinates c_b5 = new Coordinates(-1, 0);
		expectedException.expect(InvalidParameterException.class); Coordinates c_b6 = new Coordinates(0, -1);
		expectedException.expect(InvalidParameterException.class); Coordinates c_b7 = new Coordinates(481, 0);
		expectedException.expect(InvalidParameterException.class); Coordinates c_b8 = new Coordinates(0, 481);

		// Typical Cases
		Coordinates c_t1 = new Coordinates(123, 407);
		Coordinates c_t2 = new Coordinates(274, 190);
		Coordinates c_t3 = new Coordinates(6, 256);
		Coordinates c_t4 = new Coordinates(445, 77);
		expectedException.expect(InvalidParameterException.class); Coordinates c_t5 = new Coordinates(-215, 0);
		expectedException.expect(InvalidParameterException.class); Coordinates c_t6 = new Coordinates(0, -159);
		expectedException.expect(InvalidParameterException.class); Coordinates c_t7 = new Coordinates(1203, 0);
		expectedException.expect(InvalidParameterException.class); Coordinates c_t8 = new Coordinates(0, 800);
	}
} 