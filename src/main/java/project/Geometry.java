package project;

import java.util.LinkedList;

import math.geom2d.Point2D;
import math.geom2d.line.LineSegment2D;
import math.geom2d.line.Ray2D;

/**
 * Helper class to implement geometry-related functions.
 */
public final class Geometry {
    private Geometry() {}

    /**
     * The maximum allowed difference when determining the equality of two values.
     */
    public static double EQUALITY_THRESHOLD = 0.001;

    /**
     * Calculates the taxicab distance between two points.
     * @param x1 The x-coordinate of the first point.
     * @param y1 The y-coordinate of the first point.
     * @param x2 The x-coordinate of the second point.
     * @param y2 The y-coordinate of the second point.
     * @return The taxicab distance between the two points
     */
    public static int findTaxicabDistance(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    /**
     * Calculates the Euclidean distance between two points.
     * @param x1 The x-coordinate of the first point.
     * @param y1 The y-coordinate of the first point.
     * @param x2 The x-coordinate of the second point.
     * @param y2 The y-coordinate of the second point.
     * @return The Euclidean distance between the two points.
     */
    public static double findEuclideanDistance(int x1, int y1, int x2, int y2) {
        return math.geom2d.Point2D.distance(x1, y1, x2, y2);
    }
    
    /**
     * Calculates the angle between two points.
     * @param x1 The x-coordinate of the first point.
     * @param y1 The y-coordinate of the first point.
     * @param x2 The x-coordinate of the second point.
     * @param y2 The y-coordinate of the second point.
     * @return The angle in radians from the first point to the second point, as if the first point is at the origin of a polar coordinate system. The range of the angle is from 0 to 2 times PI inclusive.
     * @throws UnsupportedOperationException If the two points are the same.
     */
    public static double findAngleFrom(int x1, int y1, int x2, int y2) throws UnsupportedOperationException {
        if (x1 == y1 && x2 == y2) throw new UnsupportedOperationException("Undefined angle because the two points are the same.");
        return math.geom2d.Angle2D.angle(x1 + 1, y1, x1, y1, x2, y2);
    }

    /**
     * Finds the intersection between a ray defined by two points and an enclosing box of a specified size.
     * @param x0 The x-coordinate of the origin of the ray.
     * @param y0 The y-coordinate of the origin of the ray.
     * @param x The x-coordinate of a point in the ray.
     * @param y The y-coordinate of a point in the ray.
     * @param boxMinX The minimum x-coordinate of the box.
     * @param boxMinY The minimum y-coordinate of the box.
     * @param boxWidth The width of the box.
     * @param boxHeight The height of the box.
     * @return The Point2D object representing the intersection point between the ray and the box that is closest to (x, y). If not found, returns <code>null</code>.
     * @throws UnsupportedOperationException If the two points are the same, or either box width and box height is not greater than zero.
     */
    public static javafx.geometry.Point2D intersectBox(int x0, int y0, int x, int y, int boxMinX, int boxMinY, int boxWidth, int boxHeight) throws UnsupportedOperationException {
        if (x0 == x && y0 == y) throw new UnsupportedOperationException("Undefined line because the two points are the same.");
        if (boxWidth <= 0) throw new UnsupportedOperationException("The box width should be greater than zero.");
        if (boxHeight <= 0) throw new UnsupportedOperationException("The box height should be greater than zero.");

        Ray2D ray = new Ray2D(x0, y0, x, y);

        math.geom2d.Point2D boxCorner00 = new math.geom2d.Point2D(boxMinX, boxMinY);
        math.geom2d.Point2D boxCorner01 = new math.geom2d.Point2D(boxMinX, boxMinY + boxHeight);
        math.geom2d.Point2D boxCorner10 = new math.geom2d.Point2D(boxMinX + boxWidth, boxMinY);
        math.geom2d.Point2D boxCorner11 = new math.geom2d.Point2D(boxMinX + boxWidth, boxMinY + boxHeight);
        math.geom2d.Point2D[] corners = {boxCorner00, boxCorner01, boxCorner10, boxCorner11};
        for (math.geom2d.Point2D corner : corners) {
            if (corner.x() == x && corner.y() == y) {
                return new javafx.geometry.Point2D(corner.x(), corner.y()); 
            }
        }


        LineSegment2D boxEdge0001 = new LineSegment2D(boxCorner00, boxCorner01);
        LineSegment2D boxEdge0010 = new LineSegment2D(boxCorner00, boxCorner10);
        LineSegment2D boxEdge0111 = new LineSegment2D(boxCorner01, boxCorner11);
        LineSegment2D boxEdge1011 = new LineSegment2D(boxCorner10, boxCorner11);

        LinkedList<math.geom2d.Point2D> intersections = new LinkedList<>();
        LineSegment2D[] edges = {boxEdge0001, boxEdge0010, boxEdge0111, boxEdge1011};
        for (LineSegment2D edge : edges) {
            math.geom2d.Point2D intersection = ray.intersection(edge);
            if (intersection != null) intersections.add(intersection);
        }

        math.geom2d.Point2D p = new math.geom2d.Point2D(x, y);
        double minDistance = Double.POSITIVE_INFINITY;
        math.geom2d.Point2D minPoint = null;
        for (math.geom2d.Point2D intersection : intersections) {
            double distance = p.distance(intersection);
            if (distance < minDistance) {
                minDistance = distance;
                minPoint = intersection;
            }
        }

        return new javafx.geometry.Point2D(minPoint.x(), minPoint.y());
    }

    /**
     * Tests whether a point is within a certain distance of a ray defined by two other points.
     * @param testX The x-coordinate of the test point.
     * @param testY The y-coordinate of the test point.
     * @param x0 The x-coordinate of the origin of the ray.
     * @param y0 The y-coordinate of the origin of the ray.
     * @param x The x-coordinate of a point in the ray.
     * @param y The y-coordinate of a point in the ray.
     * @param error Allowable Euclidean distance from the ray.
     * @return Whether the test point is within the specified error of the ray.
     * @throws UnsupportedOperationException If the two points are the same, or that the error is negative.
     */
    public static boolean isInRay(int testX, int testY, int x0, int y0, int x, int y, double error) throws UnsupportedOperationException {
        if (x0 == x && y0 == y) throw new UnsupportedOperationException("Undefined line because the two points are the same.");
        if (error < 0) throw new UnsupportedOperationException("The allowable error should not be negative.");

        Ray2D ray = new Ray2D(x0, y0, x, y);

        double distance = ray.distance(testX, testY);
        return distance < error || distance - error < EQUALITY_THRESHOLD;
    }

    /**
     * Tests whether a point is within a circle with a specified point as the center.
     * @param xTest The x-coordinate of the test point.
     * @param yTest The y-coordinate of the test point.
     * @param x The x-coordinate of the center of the circle.
     * @param y The y-coordinate of the center of the circle.
     * @param r The radius of the circle.
     * @return Whether the test point is within the circle, including the boundary.
     * @throws UnsupportedOperationException If the radius is negative.
     */
    public static boolean isInCircle(int xTest, int yTest, int x, int y, double r) throws UnsupportedOperationException {
        if (r < 0) throw new UnsupportedOperationException("The allowable error should not be negative.");
        
        double distance = findEuclideanDistance(xTest, yTest, x, y);
        return distance < r || distance - r < EQUALITY_THRESHOLD;
    }

    /**
     * Tests whether a point is at another point.
     * @param xTest The x-coordinate of the test point.
     * @param yTest The y-coordinate of the test point.
     * @param x The x-coordinate of the other point.
     * @param y The y-coordinate of the other point.
     * @return Whether the test point is at the other point.
     */
    public static boolean isAt(int xTest, int yTest, int x, int y) {
        return new math.geom2d.Point2D(xTest, yTest).almostEquals(new Point2D(x, y), EQUALITY_THRESHOLD);
    }
}