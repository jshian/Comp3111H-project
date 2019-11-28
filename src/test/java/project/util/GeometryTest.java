package project.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javafx.geometry.Point2D;

/**
 * Tests the {@link Geometry} class.
 */
public class GeometryTest {
	@Rule
    public final ExpectedException expectedException = ExpectedException.none();
    
    static final double MAX_ERROR = 0.0001;
	
	@Test
	public void testTaxicabDistance() {
        // Same point
        assertEquals(0, Geometry.findTaxicabDistance(0, 0, 0, 0));
        assertEquals(0, Geometry.findTaxicabDistance(5, 0, 5, 0));
        assertEquals(0, Geometry.findTaxicabDistance(0, -10, 0, -10));
        assertEquals(0, Geometry.findTaxicabDistance(32, -456, 32, -456));

        // One equal coordinate
        assertEquals(1, Geometry.findTaxicabDistance(-1, 0, 0, 0));
        assertEquals(2, Geometry.findTaxicabDistance(0, -2, 0, 0));
        assertEquals(3, Geometry.findTaxicabDistance(0, 0, -3, 0));
        assertEquals(4, Geometry.findTaxicabDistance(0, 0, 0, -4));
        assertEquals(6, Geometry.findTaxicabDistance(6, 0, 0, 0));
        assertEquals(7, Geometry.findTaxicabDistance(0, 7, 0, 0));
        assertEquals(8, Geometry.findTaxicabDistance(0, 0, 8, 0));
        assertEquals(9, Geometry.findTaxicabDistance(0, 0, 0, 9));
        assertEquals(2, Geometry.findTaxicabDistance(-1, 0, 1, 0));
        assertEquals(4, Geometry.findTaxicabDistance(0, 2, 0, -2));

        // Distinct x- and y- coordinates
        assertEquals(3, Geometry.findTaxicabDistance(-5, 7, -3, 6));
        assertEquals(338, Geometry.findTaxicabDistance(490, 234, 436, -50));
        assertEquals(25, Geometry.findTaxicabDistance(56, 14, 43, 2));
        assertEquals(328, Geometry.findTaxicabDistance(30, -19, 329, 10));
        assertEquals(200, Geometry.findTaxicabDistance(130, -50, -40, -20));
    }
    
    @Test
    public void testEuclideanDistance() {
        // Same point
        assertEquals(0, Geometry.findEuclideanDistance(0, 0, 0, 0), MAX_ERROR);
        assertEquals(0, Geometry.findEuclideanDistance(5, 0, 5, 0), MAX_ERROR);
        assertEquals(0, Geometry.findEuclideanDistance(0, -10, 0, -10), MAX_ERROR);
        assertEquals(0, Geometry.findEuclideanDistance(32, -456, 32, -456), MAX_ERROR);

        // One equal coordinate
        assertEquals(1, Geometry.findEuclideanDistance(-1, 0, 0, 0), MAX_ERROR);
        assertEquals(2, Geometry.findEuclideanDistance(0, -2, 0, 0), MAX_ERROR);
        assertEquals(3, Geometry.findEuclideanDistance(0, 0, -3, 0), MAX_ERROR);
        assertEquals(4, Geometry.findEuclideanDistance(0, 0, 0, -4), MAX_ERROR);
        assertEquals(6, Geometry.findEuclideanDistance(6, 0, 0, 0), MAX_ERROR);
        assertEquals(7, Geometry.findEuclideanDistance(0, 7, 0, 0), MAX_ERROR);
        assertEquals(8, Geometry.findEuclideanDistance(0, 0, 8, 0), MAX_ERROR);
        assertEquals(9, Geometry.findEuclideanDistance(0, 0, 0, 9), MAX_ERROR);
        assertEquals(2, Geometry.findEuclideanDistance(-1, 0, 1, 0), MAX_ERROR);
        assertEquals(4, Geometry.findEuclideanDistance(0, 2, 0, -2), MAX_ERROR);

        // Distinct x- and y- coordinates
        assertEquals(Math.sqrt(5), Geometry.findEuclideanDistance(-5, 7, -3, 6), MAX_ERROR);
        assertEquals(Math.sqrt(83572), Geometry.findEuclideanDistance(490, 234, 436, -50), MAX_ERROR);
        assertEquals(Math.sqrt(313), Geometry.findEuclideanDistance(56, 14, 43, 2), MAX_ERROR);
        assertEquals(Math.sqrt(90242), Geometry.findEuclideanDistance(30, -19, 329, 10), MAX_ERROR);
        assertEquals(Math.sqrt(29800), Geometry.findEuclideanDistance(130, -50, -40, -20), MAX_ERROR);
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
        assertEquals(0, Geometry.findAngleFrom(-1, 0, 0, 0), MAX_ERROR);
        assertEquals(Math.PI / 2, Geometry.findAngleFrom(0, -2, 0, 0), MAX_ERROR);
        assertEquals(Math.PI, Geometry.findAngleFrom(0, 0, -3, 0), MAX_ERROR);
        assertEquals(3 * Math.PI / 2,Geometry.findAngleFrom(0, 0, 0, -4),  MAX_ERROR);
        assertEquals(Math.PI, Geometry.findAngleFrom(6, 0, 0, 0), MAX_ERROR);
        assertEquals(3 * Math.PI / 2, Geometry.findAngleFrom(0, 7, 0, 0), MAX_ERROR);
        assertEquals(0, Geometry.findAngleFrom(0, 0, 8, 0), MAX_ERROR);
        assertEquals(Math.PI / 2, Geometry.findAngleFrom(0, 0, 0, 9), MAX_ERROR);
        assertEquals(0, Geometry.findAngleFrom(-1, 0, 1, 0), MAX_ERROR);
        assertEquals(3 * Math.PI / 2, Geometry.findAngleFrom(0, 2, 0, -2), MAX_ERROR);

        // Distinct x- and y- coordinates
        assertEquals(Math.toRadians(333.43495), Geometry.findAngleFrom(-5, 7, -3, 6), MAX_ERROR);
        assertEquals(Math.toRadians(259.23424), Geometry.findAngleFrom(490, 234, 436, -50), MAX_ERROR);
        assertEquals(Math.toRadians(222.70938), Geometry.findAngleFrom(56, 14, 43, 2), MAX_ERROR);
        assertEquals(Math.toRadians(5.5397881), Geometry.findAngleFrom(30, -19, 329, 10), MAX_ERROR);
        assertEquals(Math.toRadians(169.99202), Geometry.findAngleFrom(130, -50, -40, -20), MAX_ERROR);
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
        assertEquals(0, p_boundaryTest1.getX(), MAX_ERROR);
        assertEquals(0, p_boundaryTest1.getY(), MAX_ERROR);
        Point2D p_boundaryTest2 = Geometry.intersectBox(0, 0, 30, 0, 0, 0, 30, 67);
        assertEquals(30, p_boundaryTest2.getX(), MAX_ERROR);
        assertEquals(0, p_boundaryTest2.getY(), MAX_ERROR);
        Point2D p_boundaryTest3 = Geometry.intersectBox(0, 67, 0, 0, 0, 0, 30, 67);
        assertEquals(0, p_boundaryTest3.getX(), MAX_ERROR);
        assertEquals(0, p_boundaryTest3.getY(), MAX_ERROR);
        Point2D p_boundaryTest4 = Geometry.intersectBox(0, 0, 0, 67, 0, 0, 30, 67);
        assertEquals(0, p_boundaryTest4.getX(), MAX_ERROR);
        assertEquals(67, p_boundaryTest4.getY(), MAX_ERROR);
        Point2D p_boundaryTest5 = Geometry.intersectBox(25, 0, 0, 0, -5, 0, 30, 67);
        assertEquals(-5, p_boundaryTest5.getX(), MAX_ERROR);
        assertEquals(0, p_boundaryTest5.getY(), MAX_ERROR);
        Point2D p_boundaryTest6 = Geometry.intersectBox(0, 0, 80, 0, 50, 0, 30, 67);
        assertEquals(80, p_boundaryTest6.getX(), MAX_ERROR);
        assertEquals(0, p_boundaryTest6.getY(), MAX_ERROR);
        Point2D p_boundaryTest7 = Geometry.intersectBox(-2, -5, 45, -76, -2, -5, 30, 67);
        assertEquals(-2, p_boundaryTest7.getX(), MAX_ERROR);
        assertEquals(-5, p_boundaryTest7.getY(), MAX_ERROR);
        Point2D p_boundaryTest8 = Geometry.intersectBox(45, -76, -2, -5, -2, -5, 30, 67);
        assertEquals(-2, p_boundaryTest8.getX(), MAX_ERROR);
        assertEquals(-5, p_boundaryTest8.getY(), MAX_ERROR);

        // One equal coordinate
        Point2D p_oneEqualTest1 = Geometry.intersectBox(6, 2, 10, 2, 0, 0, 50, 50);
        assertEquals(50, p_oneEqualTest1.getX(), MAX_ERROR);
        assertEquals(2, p_oneEqualTest1.getY(), MAX_ERROR);
        Point2D p_oneEqualTest2 = Geometry.intersectBox(48, 5, 8, 5, 0, 0, 345, 13);
        assertEquals(0, p_oneEqualTest2.getX(), MAX_ERROR);
        assertEquals(5, p_oneEqualTest2.getY(), MAX_ERROR);
        Point2D p_oneEqualTest3 = Geometry.intersectBox(3, 7, 3, 79, 0, 0, 296, 305);
        assertEquals(3, p_oneEqualTest3.getX(), MAX_ERROR);
        assertEquals(305, p_oneEqualTest3.getY(), MAX_ERROR);
        Point2D p_oneEqualTest4 = Geometry.intersectBox(47, 432, 47, 45, 0, 0, 479, 345);
        assertEquals(47, p_oneEqualTest4.getX(), MAX_ERROR);
        assertEquals(0, p_oneEqualTest4.getY(), MAX_ERROR);

        // Distinct x- and y- coordinates
        Point2D p_distinctTest1 = Geometry.intersectBox(-5, 7, -3, 6, -50, 0, 120, 590);
        assertEquals(9, p_distinctTest1.getX(), MAX_ERROR);
        assertEquals(0, p_distinctTest1.getY(), MAX_ERROR);
        Point2D p_distinctTest2 = Geometry.intersectBox(490, 234, 436, -50, 54, -100, 600, 340);
        assertEquals(426.49296, p_distinctTest2.getX(), MAX_ERROR);
        assertEquals(-100, p_distinctTest2.getY(), MAX_ERROR);
        Point2D p_distinctTest3 = Geometry.intersectBox(56, 14, 43, 2, -23, -50, 300, 324);
        assertEquals(-13.333333, p_distinctTest3.getX(), MAX_ERROR);
        assertEquals(-50, p_distinctTest3.getY(), MAX_ERROR);
        Point2D p_distinctTest4 = Geometry.intersectBox(30, -19, 329, 10, 0, -19, 329, 329);
        assertEquals(329, p_distinctTest4.getX(), MAX_ERROR);
        assertEquals(10, p_distinctTest4.getY(), MAX_ERROR);
        Point2D p_distinctTest5 = Geometry.intersectBox(130, -50, -40, -20, -70, -90, 315, 120);
        assertEquals(-70, p_distinctTest5.getX(), MAX_ERROR);
        assertEquals(-14.705882, p_distinctTest5.getY(), MAX_ERROR);
        Point2D p_distinctTest6 = Geometry.intersectBox(420, 460, 420, 420, 0, 0, 480, 480);
        assertEquals(420, p_distinctTest6.getX(), MAX_ERROR);
        assertEquals(0, p_distinctTest6.getY(), MAX_ERROR);
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
        assertTrue(Geometry.isInRay(0, 0, 0, 0, 1, 0, 0));
        assertTrue(Geometry.isInRay(0, -5, 0, 0, 0, -1, 0));
        assertFalse(Geometry.isInRay(1, -5, 0, 0, 0, 1, 0));
        assertTrue(Geometry.isInRay(-3, 10, 0, 0, 0, 1, 3));
        assertFalse(Geometry.isInRay(-3, 10, 0, 0, 0, 1, 2.99));
        assertTrue(Geometry.isInRay(-9, -12, 0, 0, -6, -8, 0));
        assertFalse(Geometry.isInRay(-8, -12, 0, 0, -6, -8, 0));
        assertTrue(Geometry.isInRay(-5, -15, 0, 0, -9, -12, 5));
        assertFalse(Geometry.isInRay(-5, -15, 0, 0, -9, -12, 4.99));

        // General
        assertFalse(Geometry.isInRay(0, 5, -5, 7, -3, -6, 3));
        assertTrue(Geometry.isInRay(423, -324, 490, 234, 436, -50, 60));
        assertTrue(Geometry.isInRay(82, 39, 56, 14, 43, 2, 57));
        assertFalse(Geometry.isInRay(126, -95, 30, -19, 329, 10, 83));
        assertFalse(Geometry.isInRay(-50, 30, 130, -50, -40, -20, 40));
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
        assertTrue(Geometry.isInCircle(0, 0, 0, 0, 0));
        assertFalse(Geometry.isInCircle(5, 0, 4, 0, 0));
        assertTrue(Geometry.isInCircle(0, -10, 0, -10, 0));
        assertFalse(Geometry.isInCircle(32, -456, 32, -455, 0));

        // Boundary
        assertTrue(Geometry.isInCircle(-1, 0, 0, 0, 1));
        assertTrue(Geometry.isInCircle(0, -2, 0, 0, 2));
        assertTrue(Geometry.isInCircle(0, 0, -3, 0, 3));
        assertTrue(Geometry.isInCircle(0, 0, 0, -4, 4));
        assertFalse(Geometry.isInCircle(6, 0, 0, 0, 5.99));
        assertFalse(Geometry.isInCircle(0, 7, 0, 0, 6.99));
        assertTrue(Geometry.isInCircle(0, 0, 8, 0, 8));
        assertFalse(Geometry.isInCircle(0, 0, 0, 9, 8.99));
        assertTrue(Geometry.isInCircle(-1, 0, 1, 0, 2));
        assertFalse(Geometry.isInCircle(0, 2, 0, -2, 3.99));
        assertTrue(Geometry.isInCircle(-5, 7, -3, 6, Math.sqrt(5)));
        assertFalse(Geometry.isInCircle(490, 234, 436, -50, Math.sqrt(83572) - 1));
        assertTrue(Geometry.isInCircle(56, 14, 43, 2, Math.sqrt(313)));
        assertFalse(Geometry.isInCircle(30, -19, 329, 10, Math.sqrt(90242) - 1));
        assertTrue(Geometry.isInCircle(130, -50, -40, -20, Math.sqrt(29800)));
    }

    @Test
    public void testIsAt() {
        assertTrue(Geometry.isAt(0, 0, 0, 0));
        assertFalse(Geometry.isAt(5, 0, 4, 0));
        assertTrue(Geometry.isAt(0, -10, 0, -10));
        assertFalse(Geometry.isAt(32, -456, 32, -455));
    }
}