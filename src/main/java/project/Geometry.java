package project;

import java.util.LinkedList;

import javafx.geometry.Point2D;

/**
 * Helper class to implement geometry-related functions.
 */
public final class Geometry {
    private Geometry() {}

    /**
     * Calculates the taxicab distance between two points.
     * @param x1 The x-coordinate of the first point.
     * @param y1 The y-coordinate of the first point.
     * @param x2 The x-coordinate of the second point.
     * @param y2 The y-coordinate of the second point.
     * @return The taxicab distance between the two points
     */
    public static int taxicabDistance(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    /**
     * Calculates the diagonal distance between two points.
     * @param x1 The x-coordinate of the first point.
     * @param y1 The y-coordinate of the first point.
     * @param x2 The x-coordinate of the second point.
     * @param y2 The y-coordinate of the second point.
     * @return The diagonal distance between the two points.
     */
    public static double diagonalDistance(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
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
    public static double angleFrom(int x1, int y1, int x2, int y2) throws UnsupportedOperationException {
        if (x1 == y1 && x2 == y2) throw new UnsupportedOperationException("Undefined angle because the two points are the same.");
        double angle = Math.atan2(y2 - y1, x2 - x1);
        return angle < 0 ? angle + 2 * Math.PI : angle;
    }

    /**
     * Finds the intersection between a line defined by two points and a box of a specified size with the top-left corner positioned at (0, 0).
     * @param x1 The x-coordinate of the first point in the line.
     * @param y1 The y-coordinate of the first point in the line.
     * @param x2 The x-coordinate of the second point in the line.
     * @param y2 The y-coordinate of the second point in the line.
     * @param boxWidth The width of the box.
     * @param boxHeight The height of the box.
     * @return The Point2D object representing the intersection point that is closer to the second point than it is the first point, and closest to the second point.
     */
    public static Point2D intersectBox(int x1, int y1, int x2, int y2, int boxWidth, int boxHeight) {
        // Vertical line
        if (x1 == x2) {
            if (y1 < y2) return new Point2D(x1, boxHeight); // Bottom edge

            return new Point2D(x1, 0); // Top edge
        }

        // Horizontal line
        if (y1 == y2) {
            if (x1 < x2) return new Point2D(boxWidth, y1); // Right edge

            return new Point2D(0, y1); // Left edge
        } 

        // The equation of the line is (y2-y1)/(x2-x1)=(y-y1)/(x-x1) => (y2-y1)(x-x1)=(x2-x1)(y-y1) => (y2-y1)x+(x1-x2)y+(x2y1-x1y2)=0.

        // Set y=0, x=(x1y2-x2y1)/(y2-y1).
        Point2D topPoint = new Point2D((x1 * y2 - x2 * y1) / (y2 - y1), 0);

        // Set y=boxHeight, x=(x1y2-x2y1+(x2-x1)boxHeight)/(y2-y1)
        Point2D bottomPoint = new Point2D((x1 * y2 - x2 * y1 + (x2 - x1) * boxHeight) / (y2 - y1), 0);

        // Set x=0, y=(x1y2-x2y1)/(x1-x2).
        Point2D leftPoint = new Point2D((x1 * y2 - x2 * y1) / (x1 - x2), 0);

        // Set x=boxWidth, y=(x1y2-x2y1+(y1-y2)boxWidth)/(x1-x2).
        Point2D rightPoint = new Point2D((x1 * y2 - x2 * y1 + (y1 - y2) * boxWidth) / (x1 - x2), 0);

        LinkedList<Point2D> potentialPoints = new LinkedList<>();
        if (topPoint.distance(x2, y2) < topPoint.distance(x1, y1))
            potentialPoints.add(topPoint);
        if (bottomPoint.distance(x2, y2) < bottomPoint.distance(x1, y1))
            potentialPoints.add(bottomPoint);
        if (leftPoint.distance(x2, y2) < leftPoint.distance(x1, y1))
            potentialPoints.add(leftPoint);
        if (rightPoint.distance(x2, y2) < rightPoint.distance(x1, y1))
            potentialPoints.add(rightPoint);

        Point2D minPoint = null;
        double minDist = Double.POSITIVE_INFINITY;
        for (Point2D p : potentialPoints) {
            if (p.distance(x2, y2) < minDist) {
                minPoint = p;
                minDist = p.distance(x2, y2);
            }
        }

        assert minPoint != null;

        return minPoint;
    }

    /**
     * Test whether a point is within a certain distance of a line defined by two other points, extending towards infinity.
     * @param testX The x-coordinate of the test point.
     * @param testY The y-coordinate of the test point.
     * @param x1 The x-coordinate of the first point in the line.
     * @param y1 The y-coordinate of the first point in the line.
     * @param x2 The x-coordinate of the second point in the line.
     * @param y2 The y-coordinate of the second point in the line.
     * @param error Allowable distance from the line.
     * @return Whether the test point is within the specified error of the line.
     */
    public static boolean isInLine(int testX, int testY, int x1, int y1, int x2, int y2, double error) {
        // Vertical line
        if (x1 == x2) return (testX - x1) <= error;

        // Horizontal line
        if (y1 == y2) return (testY - y1) <= error;

        // The equation of the line is (y2-y1)/(x2-x1)=(y-y1)/(x-x1) => (y2-y1)(x-x1)=(x2-x1)(y-y1) => (y2-y1)x+(x1-x2)y+(x2y1-x1y2)=0.
        // Applying the distance formula between a point and a line,
        double distance = Math.abs((y2 - y1) * testX + (x1 - x2) * testY + (x2 * y1 - x1 * y2))
                            / Math.sqrt(Math.pow(y2 - y1, 2) + Math.pow(x1 - x2, 2));

        return distance <= error;
    }

    /**
     * Test whether a point is within a circle with a specified point as the center.
     * @param xTest The x-coordinate of the test point.
     * @param yTest The y-coordinate of the test point.
     * @param x The x-coordinate of the center of the circle.
     * @param y The y-coordinate of the center of the circle.
     * @param r The radius of the circle.
     * @return Whether the test point is within the circle, including the boundary.
     */
    public static boolean isInCircle(int xTest, int yTest, int x, int y, int r) {
        return diagonalDistance(xTest, yTest, x, y) <= r;
    }
}