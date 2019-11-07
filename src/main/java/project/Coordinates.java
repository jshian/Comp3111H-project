package project;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Point2D;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Line;
import project.Arena.ExistsInArena;

import java.io.Serializable;
import java.security.InvalidParameterException;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Custom class to store 2D Cartesian coordinates of objects in the Arena
 * Coordinates are in terms of pixels.
 * @see Arena
 */
public class Coordinates implements Serializable {
    // Implement Serializable
    private static final long serialVersionUID = -1061709817920817709L;

    /**
     * The horizontal coordinate, increasing towards the right.
     */
    private IntegerProperty x = new SimpleIntegerProperty(0);

    /**
     * The vertical coordinate, increasing towards the bottom.
     */
    private IntegerProperty y = new SimpleIntegerProperty(0);

    /**
     * Default constructor for the Coordinate class. Both coordinates default to 0.
     */
    public Coordinates() {}

    /**
     * Parameterized constructor for the Coordinate class.
     * @param x The horizontal coordinate, increasing towards the right.
     * @param y The vertical coordinate, increasing towards the bottom.
     * @exception InvalidParameterException Either of the coordinates is outside the arena.
     */
    public Coordinates(int x, int y) { update(x, y); }

    /**
     * Copy constructor of Coordinates. Performs deep copy.
     * @param other The other object to copy from.
     */
    public Coordinates(Coordinates other) { update(other.getX(), other.getY()); }

    /**
     * Accesses the x-coordinate.
     * @return The horizontal coordinate, as defined in {@link Coordinates#Coordinates()}.
     */
    public int getX() { return x.get(); }

    /**
     * Accesses the y-coordinate.
     * @return The vertical coordinate, as defined in {@link Coordinates#Coordinates()}.
     */
    public int getY() { return y.get(); }

    /**
     * Binds the coordinates to an ImageView so that updating the coordinates will automatically update the UI as well.
     * @param iv The ImageView to bind.
     */
    public void bindByImage(@NonNull ImageView iv)
    {
        iv.xProperty().bind(Bindings.add(this.x, -iv.getImage().getWidth()/2));
        iv.yProperty().bind(Bindings.add(this.y, -iv.getImage().getHeight()/2));
    }

    /**
     * Updates both coordinates.
     * @param x The x-coordinate, as defined in {@link Coordinates#Coordinates()}.
     * @param y The y-coordinate, as defined in {@link Coordinates#Coordinates()}.
     * @exception IllegalArgumentException Either of the coordinates is outside the arena boundary (assumed to be 1px thick).
     */
    public void update(int x, int y) {
        final int BOUNDARY_THICKNESS = 1;
        if (x < 0 - BOUNDARY_THICKNESS|| x >= UIController.ARENA_WIDTH + BOUNDARY_THICKNESS)
            throw new IllegalArgumentException(
                String.format("The parameter 'x' is out of bounds. It should be between 0 and %d.", UIController.ARENA_WIDTH - 1));
        if (y < 0 - BOUNDARY_THICKNESS || y >= UIController.ARENA_HEIGHT + BOUNDARY_THICKNESS)
            throw new IllegalArgumentException(
                String.format("The parameter 'y' is out of bounds. It should be between 0 and %d.", UIController.ARENA_HEIGHT - 1));

        this.x.set(x);
        this.y.set(y);
    }

    /**
     * Updates both coordinates to match that of another Coordinates object.
     * @param other The other object.
     */
    public void update(Coordinates other) {
        update(other.getX(), other.getY());
    }

    /**
     * Determines whether an object in the arena is at the Cartesian coordinates represented by this Coordinate object.
     * @deprecated Please replace with {@link Geometry#isAt(int, int, int, int)}.
     * @param other The object in the arena.
     * @return Whether the object in the arena is at the Cartesian coordinates represented by this Coordinate object.
     */
    public boolean isAt(@NonNull ExistsInArena other) {
        return isAt(new Coordinates(other.getX(), other.getY()));
    }

    /**
     * Determines whether two Coordinate objects represent the same Cartesian coordinates.
     * @deprecated Please replace with {@link Geometry#isAt(int, int, int, int)}.
     * @param other The other object.
     * @return Whether the two Coordinate objects represent the same Cartesian coordinates.
     */
    public boolean isAt(@NonNull Coordinates other) { return Geometry.isAt(other.getX(), other.getY(), getX(), getY()); }

    /**
     * Calculates the taxicab distance between an object in the arena and the Cartesian coordinates represented by this Coordinate object.
     * @deprecated Please replace with {@link Geometry#findTaxicabDistance(int, int, int, int)}.
     * @param other The object in the arena.
     * @return The the taxicab distance between an object in the arena and the Cartesian coordinates represented by this Coordinate object.
     */
    public int taxicabDistanceFrom(@NonNull ExistsInArena other) {
        return taxicabDistanceFrom(new Coordinates(other.getX(), other.getY()));
    }


    /**
     * Calculates the taxicab distance between the Cartesian coordinates represented by two Coordinate objects.
     * @deprecated Please replace with {@link Geometry#findTaxicabDistance(int, int, int, int)}.
     * @param other The other object.
     * @return The taxicab distance between the Cartesian coordinates represented by the two Coordinate objects.
     */
    public int taxicabDistanceFrom(@NonNull Coordinates other) {
        return Geometry.findTaxicabDistance(getX(), getY(), other.getX(), other.getY());
    }

    /**
     * Calculates the diagonal distance between an object in the arena and the Cartesian coordinates represented by this Coordinate object.
     * @deprecated Please replace with {@link Geometry#findEuclideanDistance(int, int, int, int)}.
     * @param other The object in the arena.
     * @return The the diagonal distance between an object in the arena and the Cartesian coordinates represented by this Coordinate object.
     */
    public double diagonalDistanceFrom(@NonNull ExistsInArena other) {
        return diagonalDistanceFrom(new Coordinates(other.getX(), other.getY()));
    }

    /**
     * Calculates the diagonal distance between the Cartesian coordinates represented by two Coordinate objects.
     * @deprecated Please replace with {@link Geometry#findEuclideanDistance(int, int, int, int)}.
     * @param other The other object.
     * @return The diagonal distance between the Cartesian coordinates represented by the two Coordinate objects.
     */
    public double diagonalDistanceFrom(@NonNull Coordinates other) {
        return Geometry.findEuclideanDistance(getX(), getY(), other.getX(), other.getY());
    }

    /**
     * Calculates the angle between an object in the arena and the Cartesian coordinates represented by this Coordinate object.
     * @deprecated Please replace with {@link Geometry#findAngleFrom(int, int, int, int)}.
     * @param other The object in the arena.
     * @return The angle in radians from this object to the object in the arena, as if this object is at the origin of a polar coordinate system.
     */
    public double angleFrom(@NonNull ExistsInArena other) {
        return angleFrom(new Coordinates(other.getX(), other.getY()));
    }

    /**
     * Calculates the angle between the coordinates represented by two Coordinate objects.
     * @deprecated Please replace with {@link Geometry#findAngleFrom(int, int, int, int)}.
     * @param other The other object.
     * @return The angle in radians from this object to the other object, as if this object is at the origin of a polar coordinate system.
     */
    public double angleFrom(@NonNull Coordinates other) {
        return Geometry.findAngleFrom(getX(), getY(), other.getX(), other.getY());
    }

    /**
     * Test whether an object in the arena is within a certain distance of a line defined by this point and another object in the arena, extending towards infinity.
     * @deprecated Please replace with {@link Geometry#isInRay(int, int, int, int, int, int, double)}.
     * @param endObj The other object in the arena that represents the line, which should not be at the same coordinates as this object.
     * @param testObj The object in the arena to be tested.
     * @param error Allowable distance in terms of pixels.
     * @return Whether the test object is within the specified distance of the line.
     */
    public boolean isInLine(@NonNull ExistsInArena endObj, @NonNull ExistsInArena testObj, int error) {
        return isInLine(new Coordinates(endObj.getX(), endObj.getY()), new Coordinates(testObj.getX(), testObj.getY()), error);
    }

    /**
     * Test whether a point is within a certain distance of a line defined by this point and another point, extending towards infinity.
     * @deprecated Please replace with {@link Geometry#isInRay(int, int, int, int, int, int, double)}.
     * @param endPt The other point of the line, which should not be at the same coordinates as this object.
     * @param testPt The point to be tested.
     * @param error Allowable distance in terms of pixels.
     * @return Whether the test point is within the specified error of the line.
     */
    public boolean isInLine(@NonNull Coordinates endPt, @NonNull Coordinates testPt, int error) {
        return Geometry.isInRay(testPt.getX(), testPt.getY(), getX(), getY(), endPt.getX(), endPt.getY(), error);
    }

    /**
     * Test whether an object is within a circle with current point as the center.
     * @deprecated Please replace with {@link Geometry#isInCircle(int, int, int, int, double)}.
     * @param obj The object which to be tested.
     * @param radius The radius of the circle.
     * @return Whether the tested coordinate is in the circle.
     */
    public boolean isInCircle(@NonNull ExistsInArena obj, int radius) {
        return isInCircle(new Coordinates(obj.getX(), obj.getY()), radius);
    }

    /**
     * Test whether a point is within a circle with current point as the center.
     * @deprecated Please replace with {@link Geometry#isInCircle(int, int, int, int, double)}.
     * @param coordinates The coordinates which to be tested.
     * @param radius Whether the tested coordinate is in the circle.
     * @return Whether the tested coordinate is in the circle.
     */
    public boolean isInCircle(@NonNull Coordinates coordinates, int radius) {
        return Geometry.isInCircle(coordinates.getX(), coordinates.getY(), getX(), getY(), radius);
    }
    
    /**
     * Find the point in the edge which is in the line of the current point and given object.
     * @deprecated Please replace with {@link Geometry#intersectBox(int, int, int, int, int, int, int, int)}.
     * @param dirObj The object will form a extended line with the current point.
     * @return The point in the edge of the extended line
     */
    public Coordinates findEdgePt(@NonNull ExistsInArena dirObj) {
        return findEdgePt(new Coordinates(dirObj.getX(), dirObj.getY()));
    }

    /**
     * Find the point in the edge which is in the line of the current point and given point.
     * @deprecated Please replace with {@link Geometry#intersectBox(int, int, int, int, int, int, int, int)}.
     * @param dirPt The point will form a extended line with the current point.
     * @return  The point in the edge of the extended line.
     */
    public Coordinates findEdgePt(@NonNull Coordinates dirPt){
        Point2D point = Geometry.intersectBox(getX(), getY(), dirPt.getX(), dirPt.getY(), 0, 0, UIController.ARENA_WIDTH, UIController.ARENA_HEIGHT);
        return new Coordinates((int) Math.round(point.getX()), (int) Math.round(point.getY()));
    }

    /**
     * Draw a line from the laser tower to certain position.
     * @param obj The target.
     */
    public void drawLine(@NonNull ExistsInArena obj){
        drawLine(new Coordinates(obj.getX(),obj.getY()));
    }

    /**
     * Draw a line from the laser tower to certain position.
     * @param cor The coordinates of the target.
     */
    public void drawLine(@NonNull Coordinates cor){
        Line line = new Line(this.getX(),this.getY(),cor.getX(),cor.getY());
    }
}