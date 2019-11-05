import static org.junit.Assert.assertEquals;

import org.junit.*;
import org.junit.rules.ExpectedException;

import project.*;

public class GeometryTester {
	@Rule
	public final ExpectedException expectedException = ExpectedException.none();
	
	@Test
	public void testTaxicabDistance() {
        // Same point
        assertEquals(Geometry.taxicabDistance(0, 0, 0, 0), 0);
        assertEquals(Geometry.taxicabDistance(5, 0, 5, 0), 0);
        assertEquals(Geometry.taxicabDistance(0, -10, 0, -10), 0);
        assertEquals(Geometry.taxicabDistance(32, -456, 32, -456), 0);

        // One equal coordinate
        assertEquals(Geometry.taxicabDistance(-1, 0, 0, 0), 1);
        assertEquals(Geometry.taxicabDistance(0, -2, 0, 0), 2);
        assertEquals(Geometry.taxicabDistance(0, 0, -3, 0), 3);
        assertEquals(Geometry.taxicabDistance(0, 0, 0, -4), 4);
        assertEquals(Geometry.taxicabDistance(6, 0, 0, 0), 6);
        assertEquals(Geometry.taxicabDistance(0, 7, 0, 0), 7);
        assertEquals(Geometry.taxicabDistance(0, 0, 8, 0), 8);
        assertEquals(Geometry.taxicabDistance(0, 0, 0, 9), 9);
        assertEquals(Geometry.taxicabDistance(-1, 0, 1, 0), 2);
        assertEquals(Geometry.taxicabDistance(0, 2, 0, -2), 4);

        // Distinct x- and y- coordinates
        assertEquals(Geometry.taxicabDistance(-5, 7, -3, 6), 3);
        assertEquals(Geometry.taxicabDistance(490, 234, 436, -50), 338);
        assertEquals(Geometry.taxicabDistance(56, 14, 43, 2), 25);
        assertEquals(Geometry.taxicabDistance(30, -19, 329, 10), 328);
        assertEquals(Geometry.taxicabDistance(130, -50, -40, -20), 200);
    }
    
    @Test
    public void testDiagonalDistance() {
        final double ALLOWABLE_ERROR = 0.001;

        // Same point
        assertEquals(Geometry.diagonalDistance(0, 0, 0, 0), 0, ALLOWABLE_ERROR);
        assertEquals(Geometry.diagonalDistance(5, 0, 5, 0), 0, ALLOWABLE_ERROR);
        assertEquals(Geometry.diagonalDistance(0, -10, 0, -10), 0, ALLOWABLE_ERROR);
        assertEquals(Geometry.diagonalDistance(32, -456, 32, -456), 0, ALLOWABLE_ERROR);

        // One equal coordinate
        assertEquals(Geometry.diagonalDistance(-1, 0, 0, 0), 1, ALLOWABLE_ERROR);
        assertEquals(Geometry.diagonalDistance(0, -2, 0, 0), 2, ALLOWABLE_ERROR);
        assertEquals(Geometry.diagonalDistance(0, 0, -3, 0), 3, ALLOWABLE_ERROR);
        assertEquals(Geometry.diagonalDistance(0, 0, 0, -4), 4, ALLOWABLE_ERROR);
        assertEquals(Geometry.diagonalDistance(6, 0, 0, 0), 6, ALLOWABLE_ERROR);
        assertEquals(Geometry.diagonalDistance(0, 7, 0, 0), 7, ALLOWABLE_ERROR);
        assertEquals(Geometry.diagonalDistance(0, 0, 8, 0), 8, ALLOWABLE_ERROR);
        assertEquals(Geometry.diagonalDistance(0, 0, 0, 9), 9, ALLOWABLE_ERROR);
        assertEquals(Geometry.diagonalDistance(-1, 0, 1, 0), 2, ALLOWABLE_ERROR);
        assertEquals(Geometry.diagonalDistance(0, 2, 0, -2), 4, ALLOWABLE_ERROR);

        // Distinct x- and y- coordinates
        assertEquals(Geometry.diagonalDistance(-5, 7, -3, 6), Math.sqrt(5), ALLOWABLE_ERROR);
        assertEquals(Geometry.diagonalDistance(490, 234, 436, -50), Math.sqrt(83572), ALLOWABLE_ERROR);
        assertEquals(Geometry.diagonalDistance(56, 14, 43, 2), Math.sqrt(313), ALLOWABLE_ERROR);
        assertEquals(Geometry.diagonalDistance(30, -19, 329, 10), Math.sqrt(90242), ALLOWABLE_ERROR);
        assertEquals(Geometry.diagonalDistance(130, -50, -40, -20), Math.sqrt(29800), ALLOWABLE_ERROR);
    }

    @Test
    public void testAngleFrom() {
        final double ALLOWABLE_ERROR = 0.001;

        // Same point
        expectedException.expect(UnsupportedOperationException.class);
        assertEquals(Geometry.angleFrom(0, 0, 0, 0), 0, ALLOWABLE_ERROR);
        expectedException.expect(UnsupportedOperationException.class);
        assertEquals(Geometry.angleFrom(5, 0, 5, 0), 0, ALLOWABLE_ERROR);
        expectedException.expect(UnsupportedOperationException.class);
        assertEquals(Geometry.angleFrom(0, -10, 0, -10), 0, ALLOWABLE_ERROR);
        expectedException.expect(UnsupportedOperationException.class);
        assertEquals(Geometry.angleFrom(32, -456, 32, -456), 0, ALLOWABLE_ERROR);

        // One equal coordinate
        assertEquals(Geometry.angleFrom(-1, 0, 0, 0), 0, ALLOWABLE_ERROR);
        assertEquals(Geometry.angleFrom(0, -2, 0, 0), Math.PI / 2, ALLOWABLE_ERROR);
        assertEquals(Geometry.angleFrom(0, 0, -3, 0), Math.PI, ALLOWABLE_ERROR);
        assertEquals(Geometry.angleFrom(0, 0, 0, -4), 3 * Math.PI / 2, ALLOWABLE_ERROR);
        assertEquals(Geometry.angleFrom(6, 0, 0, 0), Math.PI, ALLOWABLE_ERROR);
        assertEquals(Geometry.angleFrom(0, 7, 0, 0), 3 * Math.PI / 2, ALLOWABLE_ERROR);
        assertEquals(Geometry.angleFrom(0, 0, 8, 0), 0, ALLOWABLE_ERROR);
        assertEquals(Geometry.angleFrom(0, 0, 0, 9), Math.PI / 2, ALLOWABLE_ERROR);
        assertEquals(Geometry.angleFrom(-1, 0, 1, 0), 0, ALLOWABLE_ERROR);
        assertEquals(Geometry.angleFrom(0, 2, 0, -2), 3 * Math.PI / 2, ALLOWABLE_ERROR);

        // Distinct x- and y- coordinates
        assertEquals(Geometry.angleFrom(-5, 7, -3, 6), Math.toRadians(301.60750), ALLOWABLE_ERROR);
        assertEquals(Geometry.angleFrom(490, 234, 436, -50), Math.toRadians(259.23424), ALLOWABLE_ERROR);
        assertEquals(Geometry.angleFrom(56, 14, 43, 2), Math.toRadians(222.70938), ALLOWABLE_ERROR);
        assertEquals(Geometry.angleFrom(30, -19, 329, 10), Math.toRadians(5.5397881), ALLOWABLE_ERROR);
        assertEquals(Geometry.angleFrom(130, -50, -40, -20), Math.toRadians(169.99202), ALLOWABLE_ERROR);
    }
} 