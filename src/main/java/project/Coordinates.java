package project;

import java.security.InvalidParameterException;
import java.util.*;

import org.checkerframework.checker.nullness.qual.NonNull;

import javafx.scene.shape.Line;
import project.Arena.ExistsInArena;
import project.Arena.TypeFilter;

/**
 * Custom class to store 2D Cartesian coordinates of objects in the arena.
 * Also stores a link to the arena itself.
 * @see Arena
 */
public class Coordinates {

    private int x = 0;
    private int y = 0;

    /**
     * Default constructor for the Coordinate class. Both coordinates default to 0.
     */
    public Coordinates() {}

    /**
     * Parametized constructor for the Coordinate class.
     * @param x The horizontal coordinate, increasing towards the right.
     * @param y The vertical coordinate, increasing towards the bottom.
     * @exception InvalidParameterException Either of the coordinates is outside the arena.
     */
    public Coordinates(int x, int y) { update(x, y); }

    /**
     * Finds all objects that occupy the specified coordinate in the arena.
     * @param coordinates The specified coordinates, which should not be <code>null</code>.
     * @return A linked list containing a reference to each object that satisfy the above criteria. Note that they do not have to be located at said coordinate.
     */
    public LinkedList<Object> findObjectsOccupying(Coordinates coordinates)
    {
        LinkedList<Object> result = new LinkedList<>();

        result.addAll(Arena.objectsAtPixel(this, EnumSet.of(TypeFilter.Projectile, TypeFilter.Monster)));
        result.addAll(Arena.objectsInGrid(this, EnumSet.of(TypeFilter.Tower)));
        
        return result;
    }

    /**
     * Accesses the x-coordinate.
     * @return The horizontal coordinate, as defined in {@link Coordinates#Coordinates()}.
     */
    public int getX() { return x; }

    /**
     * Accesses the y-coordinate.
     * @return The vertical coordinate, as defined in {@link Coordinates#Coordinates()}.
     */
    public int getY() { return y; }

    /**
     * Updates both coordinates.
     * @param x The x-coordinate, as defined in {@link Coordinates#Coordinates()}.
     * @param y The y-coordinate, as defined in {@link Coordinates#Coordinates()}.
     * @exception IllegalArgumentException Either of the coordinates is outside the arena.
     */
    public void update(int x, int y) {
        if (x < 0 || x >= UIController.ARENA_WIDTH)
            throw new IllegalArgumentException(
                String.format("The parameter 'x' is out of bounds. It should be between 0 and %d.", UIController.ARENA_WIDTH));
        if (y < 0 || y >= UIController.ARENA_HEIGHT)
            throw new IllegalArgumentException(
                String.format("The parameter 'y' is out of bounds. It should be between 0 and %d.", UIController.ARENA_HEIGHT));

        this.x = x;
        this.y = y;
    }

    /**
     * Determines whether an object in the arena is at the Cartesian coordinates represented by this Coordinate object.
     * @param other The object in the arena.
     * @return Whether the object in the arena is at the Cartesian coordinates represented by this Coordinate object.
     */
    public boolean isAt(@NonNull ExistsInArena other) {
        return isAt(other.getCoordinates());
    }

    /**
     * Determines whether two Coordinate objects represent the same Cartesian coordinates.
     * @param other The other object.
     * @return Whether the two Coordinate objects represent the same Cartesian coordinates.
     */
    public boolean isAt(@NonNull Coordinates other) {
        return (taxicabDistanceFrom(other)) == 0;
    }

    /**
     * Calculates the taxicab distance between an object in the arena and the Cartesian coordinates represented by this Coordinate object.
     * @param other The object in the arena.
     * @return The the taxicab distance between an object in the arena and the Cartesian coordinates represented by this Coordinate object.
     */
    public int taxicabDistanceFrom(@NonNull ExistsInArena other) {
        return taxicabDistanceFrom(other.getCoordinates());
    }


    /**
     * Calculates the taxicab distance between the Cartesian coordinates represented by two Coordinate objects.
     * @param other The other object.
     * @return The taxicab distance between the Cartesian coordinates represented by the two Coordinate objects.
     */
    public int taxicabDistanceFrom(@NonNull Coordinates other) {
        return this.x - other.x + this.y - other.y;
    }

    /**
     * Calculates the diagonal distance between an object in the arena and the Cartesian coordinates represented by this Coordinate object.
     * @param other The object in the arena.
     * @return The the diagonal distance between an object in the arena and the Cartesian coordinates represented by this Coordinate object.
     */
    public int diagonalDistanceFrom(@NonNull ExistsInArena other) {
        return taxicabDistanceFrom(other.getCoordinates());
    }

    /**
     * Calculates the diagonal distance between the Cartesian coordinates represented by two Coordinate objects.
     * @param other The other object.
     * @return The diagonal distance between the Cartesian coordinates represented by the two Coordinate objects.
     */
    public double diagonalDistanceFrom(@NonNull Coordinates other) {
        return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2));
    }

    /**
     * Calculates the angle between an object in the arena and the Cartesian coordinates represented by this Coordinate object.
     * @param other The object in the arena.
     * @return The angle in radians from this object to the object in the arena, as if this object is at the origin of a polar coordinate system.
     */
    public double angleFrom(@NonNull ExistsInArena other) {
        return angleFrom(other.getCoordinates());
    }

    /**
     * Calculates the angle between the coordinates represented by two Coordinate objects.
     * @param other The other object.
     * @return The angle in radians from this object to the other object, as if this object is at the origin of a polar coordinate system.
     */
    public double angleFrom(@NonNull Coordinates other) {
        return Math.atan2(other.y - this.y, other.x - this.x);
    }

    /**
     * The default allowable error when determining whether a test point is within a line.
     * Measured in radians.
     */
    private static final double DEFAULT_ERROR_LINE = 0.02;

    /**
     * Test whether an object in the arena is within a certain error of a line defined by this point and another point, extending towards infinity.
     * The default allowable error is {@value #DEFAULT_ERROR_LINE} radians.
     * @param endPt The other point of the line, whic should not be at the same coordinates as this object.
     * @param testPt The object in the arena to be tested.
     * @return Whether the test object is within the specified error of the line.
     */
    public boolean isInLine(@NonNull Coordinates endPt, @NonNull ExistsInArena testObj) {
        return isInLine(endPt, testObj.getCoordinates(), DEFAULT_ERROR_LINE);
    }

    /**
     * Test whether a point is within a certain error of a line defined by this point and another point, extending towards infinity.
     * The default allowable error is {@value #DEFAULT_ERROR_LINE} radians.
     * @param endPt The other point of the line, whic should not be at the same coordinates as this object.
     * @param testPt The point to be tested.
     * @return Whether the test point is within the specified error of the line.
     */
    public boolean isInLine(@NonNull Coordinates endPt, @NonNull Coordinates testPt) {
        return isInLine(endPt, testPt, DEFAULT_ERROR_LINE);
    }

    /**
     * Test whether a point is within a certain error of a line defined by this point and another point, extending towards infinity.
     * @param endPt The other point of the line, whic should not be at the same coordinates as this object.
     * @param testPt The point to be tested.
     * @param error Allowable error in terms of radians.
     * @return Whether the test point is within the specified error of the line.
     */
    public boolean isInLine(@NonNull Coordinates endPt, @NonNull Coordinates testPt, double error) {
        if (endPt.isAt(this)) throw new InvalidParameterException("Parameter endPt cannot be at the same coordinates as this object");

        if (testPt.isAt(this) || testPt.isAt(endPt)) return true;

        return Math.abs(this.angleFrom(endPt)-this.angleFrom(testPt)) < error;
    }

    //dont know how to implement
    public boolean isInArea(@NonNull Coordinates endPt, @NonNull Coordinates testPt) {
        return false;
    }

    //problematic: the size of arena is unknown
    public Coordinates findEdgePt(@NonNull Coordinates dirPt){
        for (int row = 0; row <= UIController.ARENA_WIDTH; ++row){
            if(this.isInLine(dirPt,new Coordinates(0,row)))
                return new Coordinates(0,row);
            if(this.isInLine(dirPt,new Coordinates(UIController.ARENA_WIDTH,row)))
                return new Coordinates(UIController.ARENA_WIDTH,row);
        }
        for (int col = 0; col <= UIController.ARENA_HEIGHT; ++col){
            if(this.isInLine(dirPt,new Coordinates(col,0)))
                return new Coordinates(col,0);
            if(this.isInLine(dirPt,new Coordinates(col,UIController.ARENA_HEIGHT)))
                return new Coordinates(col,UIController.ARENA_HEIGHT);
        }
        return dirPt;//for ignoring warning

    }

    /**
     * Draw a line from the laser tower to certain position.
     * @param cor The coordinates of the target.
     */
    public void drawLine(@NonNull Coordinates cor){
        Line line = new Line(this.x,this.y,cor.x,cor.y);//ui part incomplete
    }
}