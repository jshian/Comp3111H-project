import static org.junit.Assert.*;

import org.junit.*;
import org.junit.rules.ExpectedException;

import javafx.geometry.Point2D;
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
        // Same point
        assertEquals(Geometry.diagonalDistance(0, 0, 0, 0), 0, Geometry.EQUALITY_THRESHOLD);
        assertEquals(Geometry.diagonalDistance(5, 0, 5, 0), 0, Geometry.EQUALITY_THRESHOLD);
        assertEquals(Geometry.diagonalDistance(0, -10, 0, -10), 0, Geometry.EQUALITY_THRESHOLD);
        assertEquals(Geometry.diagonalDistance(32, -456, 32, -456), 0, Geometry.EQUALITY_THRESHOLD);

        // One equal coordinate
        assertEquals(Geometry.diagonalDistance(-1, 0, 0, 0), 1, Geometry.EQUALITY_THRESHOLD);
        assertEquals(Geometry.diagonalDistance(0, -2, 0, 0), 2, Geometry.EQUALITY_THRESHOLD);
        assertEquals(Geometry.diagonalDistance(0, 0, -3, 0), 3, Geometry.EQUALITY_THRESHOLD);
        assertEquals(Geometry.diagonalDistance(0, 0, 0, -4), 4, Geometry.EQUALITY_THRESHOLD);
        assertEquals(Geometry.diagonalDistance(6, 0, 0, 0), 6, Geometry.EQUALITY_THRESHOLD);
        assertEquals(Geometry.diagonalDistance(0, 7, 0, 0), 7, Geometry.EQUALITY_THRESHOLD);
        assertEquals(Geometry.diagonalDistance(0, 0, 8, 0), 8, Geometry.EQUALITY_THRESHOLD);
        assertEquals(Geometry.diagonalDistance(0, 0, 0, 9), 9, Geometry.EQUALITY_THRESHOLD);
        assertEquals(Geometry.diagonalDistance(-1, 0, 1, 0), 2, Geometry.EQUALITY_THRESHOLD);
        assertEquals(Geometry.diagonalDistance(0, 2, 0, -2), 4, Geometry.EQUALITY_THRESHOLD);

        // Distinct x- and y- coordinates
        assertEquals(Geometry.diagonalDistance(-5, 7, -3, 6), Math.sqrt(5), Geometry.EQUALITY_THRESHOLD);
        assertEquals(Geometry.diagonalDistance(490, 234, 436, -50), Math.sqrt(83572), Geometry.EQUALITY_THRESHOLD);
        assertEquals(Geometry.diagonalDistance(56, 14, 43, 2), Math.sqrt(313), Geometry.EQUALITY_THRESHOLD);
        assertEquals(Geometry.diagonalDistance(30, -19, 329, 10), Math.sqrt(90242), Geometry.EQUALITY_THRESHOLD);
        assertEquals(Geometry.diagonalDistance(130, -50, -40, -20), Math.sqrt(29800), Geometry.EQUALITY_THRESHOLD);
    }

    @Test
    public void testAngleFrom() {
        // Same point
        expectedException.expect(UnsupportedOperationException.class);
        assertEquals(Geometry.angleFrom(0, 0, 0, 0), 0, Geometry.EQUALITY_THRESHOLD);
        expectedException.expect(UnsupportedOperationException.class);
        assertEquals(Geometry.angleFrom(5, 0, 5, 0), 0, Geometry.EQUALITY_THRESHOLD);
        expectedException.expect(UnsupportedOperationException.class);
        assertEquals(Geometry.angleFrom(0, -10, 0, -10), 0, Geometry.EQUALITY_THRESHOLD);
        expectedException.expect(UnsupportedOperationException.class);
        assertEquals(Geometry.angleFrom(32, -456, 32, -456), 0, Geometry.EQUALITY_THRESHOLD);

        // One equal coordinate
        assertEquals(Geometry.angleFrom(-1, 0, 0, 0), 0, Geometry.EQUALITY_THRESHOLD);
        assertEquals(Geometry.angleFrom(0, -2, 0, 0), Math.PI / 2, Geometry.EQUALITY_THRESHOLD);
        assertEquals(Geometry.angleFrom(0, 0, -3, 0), Math.PI, Geometry.EQUALITY_THRESHOLD);
        assertEquals(Geometry.angleFrom(0, 0, 0, -4), 3 * Math.PI / 2, Geometry.EQUALITY_THRESHOLD);
        assertEquals(Geometry.angleFrom(6, 0, 0, 0), Math.PI, Geometry.EQUALITY_THRESHOLD);
        assertEquals(Geometry.angleFrom(0, 7, 0, 0), 3 * Math.PI / 2, Geometry.EQUALITY_THRESHOLD);
        assertEquals(Geometry.angleFrom(0, 0, 8, 0), 0, Geometry.EQUALITY_THRESHOLD);
        assertEquals(Geometry.angleFrom(0, 0, 0, 9), Math.PI / 2, Geometry.EQUALITY_THRESHOLD);
        assertEquals(Geometry.angleFrom(-1, 0, 1, 0), 0, Geometry.EQUALITY_THRESHOLD);
        assertEquals(Geometry.angleFrom(0, 2, 0, -2), 3 * Math.PI / 2, Geometry.EQUALITY_THRESHOLD);

        // Distinct x- and y- coordinates
        assertEquals(Geometry.angleFrom(-5, 7, -3, 6), Math.toRadians(301.60750), Geometry.EQUALITY_THRESHOLD);
        assertEquals(Geometry.angleFrom(490, 234, 436, -50), Math.toRadians(259.23424), Geometry.EQUALITY_THRESHOLD);
        assertEquals(Geometry.angleFrom(56, 14, 43, 2), Math.toRadians(222.70938), Geometry.EQUALITY_THRESHOLD);
        assertEquals(Geometry.angleFrom(30, -19, 329, 10), Math.toRadians(5.5397881), Geometry.EQUALITY_THRESHOLD);
        assertEquals(Geometry.angleFrom(130, -50, -40, -20), Math.toRadians(169.99202), Geometry.EQUALITY_THRESHOLD);
    }

    @Test
    public void testIntersectBox() {
        
        // Same point
        expectedException.expect(UnsupportedOperationException.class);
        Point2D p1 = Geometry.intersectBox(0, 0, 0, 0, 10, 20);
        expectedException.expect(UnsupportedOperationException.class);
        Point2D p2 = Geometry.intersectBox(5, 0, 5, 0, 30, 4);
        expectedException.expect(UnsupportedOperationException.class);
        Point2D p3 = Geometry.intersectBox(0, 10, 0, 10, 230, 495);
        expectedException.expect(UnsupportedOperationException.class);
        Point2D p4 = Geometry.intersectBox(32, 456, 32, 456, 534, 54);

        // Invalid box size
        expectedException.expect(UnsupportedOperationException.class);
        Point2D p5 = Geometry.intersectBox(0, 1, 2, 3, -1, 2);
        expectedException.expect(UnsupportedOperationException.class);
        Point2D p6 = Geometry.intersectBox(3, 2, 1, 0, 8, -43);
        expectedException.expect(UnsupportedOperationException.class);
        Point2D p7 = Geometry.intersectBox(1, 3, 2, 0, -10, -8);

        // Outside of box
        expectedException.expect(UnsupportedOperationException.class);
        Point2D p8 = Geometry.intersectBox(30, 0, 0, 0, 8, 10);
        expectedException.expect(UnsupportedOperationException.class);
        Point2D p9 = Geometry.intersectBox(0, 4, 0, 0, 5, 3);
        expectedException.expect(UnsupportedOperationException.class);
        Point2D p10 = Geometry.intersectBox(0, 0, 63, 0, 61, 7);
        expectedException.expect(UnsupportedOperationException.class);
        Point2D p11 = Geometry.intersectBox(0, 0, 0, 56, 4, 42);
        expectedException.expect(UnsupportedOperationException.class);
        Point2D p12 = Geometry.intersectBox(-1, 0, 0, 0, 8, 10);
        expectedException.expect(UnsupportedOperationException.class);
        Point2D p13 = Geometry.intersectBox(0, -1, 0, 0, 5, 3);
        expectedException.expect(UnsupportedOperationException.class);
        Point2D p14 = Geometry.intersectBox(0, 0, -1, 0, 61, 7);
        expectedException.expect(UnsupportedOperationException.class);
        Point2D p15 = Geometry.intersectBox(0, 0, 0, -1, 4, 42);

        // Boundary of box
        Point2D p16 = Geometry.intersectBox(30, 0, 0, 0, 30, 67);
        assertEquals(p16.getX(), 0, Geometry.EQUALITY_THRESHOLD);
        assertEquals(p16.getY(), 0, Geometry.EQUALITY_THRESHOLD);
        Point2D p17 = Geometry.intersectBox(0, 0, 30, 0, 30, 67);
        assertEquals(p17.getX(), 30, Geometry.EQUALITY_THRESHOLD);
        assertEquals(p17.getY(), 0, Geometry.EQUALITY_THRESHOLD);
        Point2D p18 = Geometry.intersectBox(0, 67, 0, 0, 30, 67);
        assertEquals(p18.getX(), 0, Geometry.EQUALITY_THRESHOLD);
        assertEquals(p18.getY(), 0, Geometry.EQUALITY_THRESHOLD);
        Point2D p19 = Geometry.intersectBox(0, 0, 0, 67, 30, 67);
        assertEquals(p19.getX(), 0, Geometry.EQUALITY_THRESHOLD);
        assertEquals(p19.getY(), 67, Geometry.EQUALITY_THRESHOLD);

        // One equal coordinate
        Point2D p20 = Geometry.intersectBox(6, 2, 10, 2, 50, 50);
        assertEquals(p20.getX(), 50, Geometry.EQUALITY_THRESHOLD);
        assertEquals(p20.getY(), 2, Geometry.EQUALITY_THRESHOLD);
        Point2D p21 = Geometry.intersectBox(48, 5, 8, 5, 345, 13);
        assertEquals(p21.getX(), 0 , Geometry.EQUALITY_THRESHOLD);
        assertEquals(p21.getY(), 5, Geometry.EQUALITY_THRESHOLD);
        Point2D p22 = Geometry.intersectBox(3, 7, 3, 79, 296, 305);
        assertEquals(p22.getX(), 0, Geometry.EQUALITY_THRESHOLD);
        assertEquals(p22.getY(), 305, Geometry.EQUALITY_THRESHOLD);
        Point2D p23 = Geometry.intersectBox(47, 432, 47, 45, 479, 345);
        assertEquals(p23.getX(), 47, Geometry.EQUALITY_THRESHOLD);
        assertEquals(p23.getY(), 0, Geometry.EQUALITY_THRESHOLD);

        // Distinct x- and y- coordinates
        // Point2D p24 = Geometry.intersectBox(-5, 7, -3, 6), Math.toRadians(301.60750), ALLOWABLE_ERROR);
        // Point2D p25 = Geometry.intersectBox(490, 234, 436, -50), Math.toRadians(259.23424), ALLOWABLE_ERROR);
        // Point2D p26 = Geometry.intersectBox(56, 14, 43, 2), Math.toRadians(222.70938), ALLOWABLE_ERROR);
        // Point2D p27 = Geometry.intersectBox(30, -19, 329, 10), Math.toRadians(5.5397881), ALLOWABLE_ERROR);
        // Point2D p28 = Geometry.intersectBox(130, -50, -40, -20), Math.toRadians(169.99202), ALLOWABLE_ERROR);
    }
} 