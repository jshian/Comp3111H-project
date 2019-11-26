package project.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javafx.geometry.Point2D;

/**
 * Tests the {@link Geometry} class.
 */
public class GeometryTester {
	@Rule
    public final ExpectedException expectedException = ExpectedException.none();
    
    static final double MAX_ERROR = 0.0001;
	
	@Test
	public void testTaxicabDistance() {
        // Same point
        assertEquals(Geometry.findTaxicabDistance(0, 0, 0, 0), 0);
        assertEquals(Geometry.findTaxicabDistance(5, 0, 5, 0), 0);
        assertEquals(Geometry.findTaxicabDistance(0, -10, 0, -10), 0);
        assertEquals(Geometry.findTaxicabDistance(32, -456, 32, -456), 0);

        // One equal coordinate
        assertEquals(Geometry.findTaxicabDistance(-1, 0, 0, 0), 1);
        assertEquals(Geometry.findTaxicabDistance(0, -2, 0, 0), 2);
        assertEquals(Geometry.findTaxicabDistance(0, 0, -3, 0), 3);
        assertEquals(Geometry.findTaxicabDistance(0, 0, 0, -4), 4);
        assertEquals(Geometry.findTaxicabDistance(6, 0, 0, 0), 6);
        assertEquals(Geometry.findTaxicabDistance(0, 7, 0, 0), 7);
        assertEquals(Geometry.findTaxicabDistance(0, 0, 8, 0), 8);
        assertEquals(Geometry.findTaxicabDistance(0, 0, 0, 9), 9);
        assertEquals(Geometry.findTaxicabDistance(-1, 0, 1, 0), 2);
        assertEquals(Geometry.findTaxicabDistance(0, 2, 0, -2), 4);

        // Distinct x- and y- coordinates
        assertEquals(Geometry.findTaxicabDistance(-5, 7, -3, 6), 3);
        assertEquals(Geometry.findTaxicabDistance(490, 234, 436, -50), 338);
        assertEquals(Geometry.findTaxicabDistance(56, 14, 43, 2), 25);
        assertEquals(Geometry.findTaxicabDistance(30, -19, 329, 10), 328);
        assertEquals(Geometry.findTaxicabDistance(130, -50, -40, -20), 200);
    }
    
    @Test
    public void testEuclideanDistance() {
        // Same point
        assertEquals(Geometry.findEuclideanDistance(0, 0, 0, 0), 0, MAX_ERROR);
        assertEquals(Geometry.findEuclideanDistance(5, 0, 5, 0), 0, MAX_ERROR);
        assertEquals(Geometry.findEuclideanDistance(0, -10, 0, -10), 0, MAX_ERROR);
        assertEquals(Geometry.findEuclideanDistance(32, -456, 32, -456), 0, MAX_ERROR);

        // One equal coordinate
        assertEquals(Geometry.findEuclideanDistance(-1, 0, 0, 0), 1, MAX_ERROR);
        assertEquals(Geometry.findEuclideanDistance(0, -2, 0, 0), 2, MAX_ERROR);
        assertEquals(Geometry.findEuclideanDistance(0, 0, -3, 0), 3, MAX_ERROR);
        assertEquals(Geometry.findEuclideanDistance(0, 0, 0, -4), 4, MAX_ERROR);
        assertEquals(Geometry.findEuclideanDistance(6, 0, 0, 0), 6, MAX_ERROR);
        assertEquals(Geometry.findEuclideanDistance(0, 7, 0, 0), 7, MAX_ERROR);
        assertEquals(Geometry.findEuclideanDistance(0, 0, 8, 0), 8, MAX_ERROR);
        assertEquals(Geometry.findEuclideanDistance(0, 0, 0, 9), 9, MAX_ERROR);
        assertEquals(Geometry.findEuclideanDistance(-1, 0, 1, 0), 2, MAX_ERROR);
        assertEquals(Geometry.findEuclideanDistance(0, 2, 0, -2), 4, MAX_ERROR);

        // Distinct x- and y- coordinates
        assertEquals(Geometry.findEuclideanDistance(-5, 7, -3, 6), Math.sqrt(5), MAX_ERROR);
        assertEquals(Geometry.findEuclideanDistance(490, 234, 436, -50), Math.sqrt(83572), MAX_ERROR);
        assertEquals(Geometry.findEuclideanDistance(56, 14, 43, 2), Math.sqrt(313), MAX_ERROR);
        assertEquals(Geometry.findEuclideanDistance(30, -19, 329, 10), Math.sqrt(90242), MAX_ERROR);
        assertEquals(Geometry.findEuclideanDistance(130, -50, -40, -20), Math.sqrt(29800), MAX_ERROR);
    }

    public void assertUnsupportedOperationException_findAngleFrom(int x1, int y1, int x2, int y2) {
        try {
            Geometry.findAngleFrom(x1, y1, x2, y2);
            fail(String.format("findAngleFrom with parameters (%d, %d, %d, %d) should have thrown an exception.", x1, y1, x2, y2));
        } catch (UnsupportedOperationException e) {

        }
    }

    @Test
    public void testAngleFrom() {
        // Same point
        assertUnsupportedOperationException_findAngleFrom(0, 0, 0, 0);
        assertUnsupportedOperationException_findAngleFrom(5, 0, 5, 0);
        assertUnsupportedOperationException_findAngleFrom(0, -10, 0, -10);
        assertUnsupportedOperationException_findAngleFrom(32, -456, 32, -456);

        // One equal coordinate
        assertEquals(Geometry.findAngleFrom(-1, 0, 0, 0), 0, MAX_ERROR);
        assertEquals(Geometry.findAngleFrom(0, -2, 0, 0), Math.PI / 2, MAX_ERROR);
        assertEquals(Geometry.findAngleFrom(0, 0, -3, 0), Math.PI, MAX_ERROR);
        assertEquals(Geometry.findAngleFrom(0, 0, 0, -4), 3 * Math.PI / 2, MAX_ERROR);
        assertEquals(Geometry.findAngleFrom(6, 0, 0, 0), Math.PI, MAX_ERROR);
        assertEquals(Geometry.findAngleFrom(0, 7, 0, 0), 3 * Math.PI / 2, MAX_ERROR);
        assertEquals(Geometry.findAngleFrom(0, 0, 8, 0), 0, MAX_ERROR);
        assertEquals(Geometry.findAngleFrom(0, 0, 0, 9), Math.PI / 2, MAX_ERROR);
        assertEquals(Geometry.findAngleFrom(-1, 0, 1, 0), 0, MAX_ERROR);
        assertEquals(Geometry.findAngleFrom(0, 2, 0, -2), 3 * Math.PI / 2, MAX_ERROR);

        // Distinct x- and y- coordinates
        assertEquals(Geometry.findAngleFrom(-5, 7, -3, 6), Math.toRadians(333.43495), MAX_ERROR);
        assertEquals(Geometry.findAngleFrom(490, 234, 436, -50), Math.toRadians(259.23424), MAX_ERROR);
        assertEquals(Geometry.findAngleFrom(56, 14, 43, 2), Math.toRadians(222.70938), MAX_ERROR);
        assertEquals(Geometry.findAngleFrom(30, -19, 329, 10), Math.toRadians(5.5397881), MAX_ERROR);
        assertEquals(Geometry.findAngleFrom(130, -50, -40, -20), Math.toRadians(169.99202), MAX_ERROR);
    }
    
    public void assertUnsupportedOperationException_intersectBox(int x0, int y0, int x, int y, int boxMinX, int boxMinY, int boxWidth, int boxHeight) {
        try {
            Geometry.intersectBox(x0, y0, x, y, boxMinX, boxMinY, boxWidth, boxHeight);
            fail(String.format("intersectBox with parameters (%d, %d, %d, %d, %d, %d, %d, %d) should have thrown an exception.", x0, y0, x, y, boxMinX, boxMinY, boxWidth, boxHeight));
        } catch (UnsupportedOperationException e) {

        }
    }

    @Test
    public void testIntersectBox() {
        // Same point
    	assertUnsupportedOperationException_intersectBox(0, 0, 0, 0, -1, -1, 2, 2);
    	assertUnsupportedOperationException_intersectBox(-1, 0, -1, 0, -2, -2, 2, 2);
    	assertUnsupportedOperationException_intersectBox(0, 1, 0, 1, 0, 0, 2, 2);
    	assertUnsupportedOperationException_intersectBox(-1, -1, -1, -1, -2, -2, 2, 2);

        // Invalid box size
    	assertUnsupportedOperationException_intersectBox(0, 0, 1, 1, 2, -1, -4, 4);
    	assertUnsupportedOperationException_intersectBox(0, 0, 1, 1, -1, 2, 4, -4);
    	assertUnsupportedOperationException_intersectBox(0, 0, 1, 1, 0, -1, 0, 4);
    	assertUnsupportedOperationException_intersectBox(0, 0, 1, 1, -1, 0, 4, 0);
    	assertUnsupportedOperationException_intersectBox(0, 0, 1, 1, 2, 2, -4, -4);
    	assertUnsupportedOperationException_intersectBox(0, 0, 1, 1, 0, 0, 0, 0);

        // Ray does not touch box
    	assertNull(Geometry.intersectBox(0, 0, 2, 2, 2, 0, 1, 1));
    	assertNull(Geometry.intersectBox(0, 0, 2, 2, 0, 2, 1, 1));
    	assertNull(Geometry.intersectBox(0, 0, 2, 2, -2, -2, 1, 1));
    	assertNull(Geometry.intersectBox(0, 0, -2, -2, 1, 1, 1, 1));
    	assertNull(Geometry.intersectBox(13, -34, -3, 2, -1, -2, 71, 40));

        // Boundary of box
        Point2D p_boundaryTest1 = Geometry.intersectBox(30, 0, 0, 0, 0, 0, 30, 67);
        assertEquals(p_boundaryTest1.getX(), 0, MAX_ERROR);
        assertEquals(p_boundaryTest1.getY(), 0, MAX_ERROR);
        Point2D p_boundaryTest2 = Geometry.intersectBox(0, 0, 30, 0, 0, 0, 30, 67);
        assertEquals(p_boundaryTest2.getX(), 30, MAX_ERROR);
        assertEquals(p_boundaryTest2.getY(), 0, MAX_ERROR);
        Point2D p_boundaryTest3 = Geometry.intersectBox(0, 67, 0, 0, 0, 0, 30, 67);
        assertEquals(p_boundaryTest3.getX(), 0, MAX_ERROR);
        assertEquals(p_boundaryTest3.getY(), 0, MAX_ERROR);
        Point2D p_boundaryTest4 = Geometry.intersectBox(0, 0, 0, 67, 0, 0, 30, 67);
        assertEquals(p_boundaryTest4.getX(), 0, MAX_ERROR);
        assertEquals(p_boundaryTest4.getY(), 67, MAX_ERROR);
        Point2D p_boundaryTest5 = Geometry.intersectBox(25, 0, 0, 0, -5, 0, 30, 67);
        assertEquals(p_boundaryTest5.getX(), -5, MAX_ERROR);
        assertEquals(p_boundaryTest5.getY(), 0, MAX_ERROR);
        Point2D p_boundaryTest6 = Geometry.intersectBox(0, 0, 80, 0, 50, 0, 30, 67);
        assertEquals(p_boundaryTest6.getX(), 80, MAX_ERROR);
        assertEquals(p_boundaryTest6.getY(), 0, MAX_ERROR);
        Point2D p_boundaryTest7 = Geometry.intersectBox(-2, -5, 45, -76, -2, -5, 30, 67);
        assertEquals(p_boundaryTest7.getX(), -2, MAX_ERROR);
        assertEquals(p_boundaryTest7.getY(), -5, MAX_ERROR);
        Point2D p_boundaryTest8 = Geometry.intersectBox(45, -76, -2, -5, -2, -5, 30, 67);
        assertEquals(p_boundaryTest8.getX(), -2, MAX_ERROR);
        assertEquals(p_boundaryTest8.getY(), -5, MAX_ERROR);

        // One equal coordinate
        Point2D p_oneEqualTest1 = Geometry.intersectBox(6, 2, 10, 2, 0, 0, 50, 50);
        assertEquals(p_oneEqualTest1.getX(), 50, MAX_ERROR);
        assertEquals(p_oneEqualTest1.getY(), 2, MAX_ERROR);
        Point2D p_oneEqualTest2 = Geometry.intersectBox(48, 5, 8, 5, 0, 0, 345, 13);
        assertEquals(p_oneEqualTest2.getX(), 0 , MAX_ERROR);
        assertEquals(p_oneEqualTest2.getY(), 5, MAX_ERROR);
        Point2D p_oneEqualTest3 = Geometry.intersectBox(3, 7, 3, 79, 0, 0, 296, 305);
        assertEquals(p_oneEqualTest3.getX(), 3, MAX_ERROR);
        assertEquals(p_oneEqualTest3.getY(), 305, MAX_ERROR);
        Point2D p_oneEqualTest4 = Geometry.intersectBox(47, 432, 47, 45, 0, 0, 479, 345);
        assertEquals(p_oneEqualTest4.getX(), 47, MAX_ERROR);
        assertEquals(p_oneEqualTest4.getY(), 0, MAX_ERROR);

        // Distinct x- and y- coordinates
        Point2D p_distinctTest1 = Geometry.intersectBox(-5, 7, -3, 6, -50, 0, 120, 590);
        assertEquals(p_distinctTest1.getX(), 9, MAX_ERROR);
        assertEquals(p_distinctTest1.getY(), 0, MAX_ERROR);
        Point2D p_distinctTest2 = Geometry.intersectBox(490, 234, 436, -50, 54, -100, 600, 340);
        assertEquals(p_distinctTest2.getX(), 426.49296, MAX_ERROR);
        assertEquals(p_distinctTest2.getY(), -100, MAX_ERROR);
        Point2D p_distinctTest3 = Geometry.intersectBox(56, 14, 43, 2, -23, -50, 300, 324);
        assertEquals(p_distinctTest3.getX(), -13.333333, MAX_ERROR);
        assertEquals(p_distinctTest3.getY(), -50, MAX_ERROR);
        Point2D p_distinctTest4 = Geometry.intersectBox(30, -19, 329, 10, 0, -19, 329, 329);
        assertEquals(p_distinctTest4.getX(), 329, MAX_ERROR);
        assertEquals(p_distinctTest4.getY(), 10, MAX_ERROR);
        Point2D p_distinctTest5 = Geometry.intersectBox(130, -50, -40, -20, -70, -90, 315, 120);
        assertEquals(p_distinctTest5.getX(), -70, MAX_ERROR);
        assertEquals(p_distinctTest5.getY(), -14.705882, MAX_ERROR);
        Point2D p_distinctTest6 = Geometry.intersectBox(420, 460, 420, 420, 0, 0, 480, 480);
        assertEquals(p_distinctTest6.getX(), 420, MAX_ERROR);
        assertEquals(p_distinctTest6.getY(), 0, MAX_ERROR);
    }
    
    public void assertUnsupportedOperationException_isInRay(int testX, int testY, int x0, int y0, int x, int y, int maxDist) {
        try {
            Geometry.isInRay(testX, testY, x0, y0, x, y, maxDist);
            fail(String.format("isInRay with parameters (%d, %d, %d, %d, %d, %d, %d) should have thrown an exception.", testX, testY, x0, y0, x, y, maxDist));
        } catch (UnsupportedOperationException e) {

        }
    }

    @Test
    public void testIsInRay(){
        // Same point
    	assertUnsupportedOperationException_isInRay(4, 6, 0, 0, 0, 0, 3);
    	assertUnsupportedOperationException_isInRay(-3, -4, 5, 0, 5, 0, 3);
    	assertUnsupportedOperationException_isInRay(20, -43, 0, -10, 0, -10, 3);
    	assertUnsupportedOperationException_isInRay(-23, 45, 32, -456, 32, -456, 3);

        // Boundary
        assertEquals(Geometry.isInRay(0, 0, 0, 0, 1, 0, 0), true);
        assertEquals(Geometry.isInRay(0, -5, 0, 0, 0, -1, 0), true);
        assertEquals(Geometry.isInRay(1, -5, 0, 0, 0, 1, 0), false);
        assertEquals(Geometry.isInRay(-3, 10, 0, 0, 0, 1, 3), true);
        assertEquals(Geometry.isInRay(-3, 10, 0, 0, 0, 1, 2.99), false);
        assertEquals(Geometry.isInRay(-9, -12, 0, 0, -6, -8, 0), true);
        assertEquals(Geometry.isInRay(-8, -12, 0, 0, -6, -8, 0), false);
        assertEquals(Geometry.isInRay(-5, -15, 0, 0, -9, -12, 5), true);
        assertEquals(Geometry.isInRay(-5, -15, 0, 0, -9, -12, 4.99), false);

        // General
        assertEquals(Geometry.isInRay(0, 5, -5, 7, -3, -6, 3), false);
        assertEquals(Geometry.isInRay(423, -324, 490, 234, 436, -50, 60), true);
        assertEquals(Geometry.isInRay(82, 39, 56, 14, 43, 2, 57), true);
        assertEquals(Geometry.isInRay(126, -95, 30, -19, 329, 10, 83), false);
        assertEquals(Geometry.isInRay(-50, 30, 130, -50, -40, -20, 40), false);
    }
    
    public void assertUnsupportedOperationException_isInCircle(int testX, int testY, int x0, int y0, double r) {
        try {
            Geometry.isInCircle(testX, testY, x0, y0, r);
            fail(String.format("isInCircle with parameters (%d, %d, %d, %d, %.2f) should have thrown an exception.", testX, testY, x0, y0, r));
        } catch (UnsupportedOperationException e) {

        }
    }
    
    @Test
    public void testIsInCircle() {
        // Negative radius
    	assertUnsupportedOperationException_isInCircle(0, 0, 0, 0, -1);
    	assertUnsupportedOperationException_isInCircle(5, 0, 5, 0, -2);
    	assertUnsupportedOperationException_isInCircle(0, -10, 0, -10, -7);
    	assertUnsupportedOperationException_isInCircle(32, -456, 32, -456, -12);

        // Zero radius
        assertEquals(Geometry.isInCircle(0, 0, 0, 0, 0), true);
        assertEquals(Geometry.isInCircle(5, 0, 4, 0, 0), false);
        assertEquals(Geometry.isInCircle(0, -10, 0, -10, 0), true);
        assertEquals(Geometry.isInCircle(32, -456, 32, -455, 0), false);

        // Boundary
        assertEquals(Geometry.isInCircle(-1, 0, 0, 0, 1), true);
        assertEquals(Geometry.isInCircle(0, -2, 0, 0, 2), true);
        assertEquals(Geometry.isInCircle(0, 0, -3, 0, 3), true);
        assertEquals(Geometry.isInCircle(0, 0, 0, -4, 4), true);
        assertEquals(Geometry.isInCircle(6, 0, 0, 0, 5.99), false);
        assertEquals(Geometry.isInCircle(0, 7, 0, 0, 6.99), false);
        assertEquals(Geometry.isInCircle(0, 0, 8, 0, 8), true);
        assertEquals(Geometry.isInCircle(0, 0, 0, 9, 8.99), false);
        assertEquals(Geometry.isInCircle(-1, 0, 1, 0, 2), true);
        assertEquals(Geometry.isInCircle(0, 2, 0, -2, 3.99), false);
        assertEquals(Geometry.isInCircle(-5, 7, -3, 6, Math.sqrt(5)), true);
        assertEquals(Geometry.isInCircle(490, 234, 436, -50, Math.sqrt(83572 - 1)), false);
        assertEquals(Geometry.isInCircle(56, 14, 43, 2, Math.sqrt(313)), true);
        assertEquals(Geometry.isInCircle(30, -19, 329, 10, Math.sqrt(90242 - 1)), false);
        assertEquals(Geometry.isInCircle(130, -50, -40, -20, Math.sqrt(29800)), true);
    }

    @Test
    public void testIsAt() {
        assertEquals(Geometry.isAt(0, 0, 0, 0), true);
        assertEquals(Geometry.isAt(5, 0, 4, 0), false);
        assertEquals(Geometry.isAt(0, -10, 0, -10), true);
        assertEquals(Geometry.isAt(32, -456, 32, -455), false);
    }
}