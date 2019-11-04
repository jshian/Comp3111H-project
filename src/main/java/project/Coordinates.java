package project;

import project.Arena.ExistsInArena;
import project.Arena.TypeFilter;

import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.*;

import org.checkerframework.checker.nullness.qual.NonNull;

import javafx.scene.shape.Line;
import project.monsters.Monster;

/**
 * Custom class to store 2D Cartesian coordinates of objects in the arena.
 * Also stores a link to the arena itself.
 * @see Arena
 */
public class Coordinates implements Serializable {
    // Implement Serializable
    private static final long serialVersionUID = -1061709817920817709L;

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
                String.format("The parameter 'x' is out of bounds. It should be between 0 and %d.", UIController.ARENA_WIDTH - 1));
        if (y < 0 || y >= UIController.ARENA_HEIGHT)
            throw new IllegalArgumentException(
                String.format("The parameter 'y' is out of bounds. It should be between 0 and %d.", UIController.ARENA_HEIGHT - 1));

        this.x = x;
        this.y = y;
    }

    /**
     * Determines whether an object in the arena is at the Cartesian coordinates represented by this Coordinate object.
     * @param other The object in the arena.
     * @return Whether the object in the arena is at the Cartesian coordinates represented by this Coordinate object.
     */
    public boolean isAt(@NonNull ExistsInArena other) {
        return isAt(new Coordinates(other.getX(), other.getY()));
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
        return taxicabDistanceFrom(new Coordinates(other.getX(), other.getY()));
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
    public double diagonalDistanceFrom(@NonNull ExistsInArena other) {
        return diagonalDistanceFrom(new Coordinates(other.getX(), other.getY()));
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
        return angleFrom(new Coordinates(other.getX(), other.getY()));
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
     * Test whether an object in the arena is within a certain error of a line defined by this point and another object in the arena, extending towards infinity.
     * The default allowable error is {@value #DEFAULT_ERROR_LINE} radians.
     * @param endObj The other object in the arena that represents the line, which should not be at the same coordinates as this object.
     * @param testObj The object in the arena to be tested.
     * @return Whether the test object is within the specified error of the line.
     */
    public boolean isInLine(@NonNull ExistsInArena endObj, @NonNull ExistsInArena testObj) {
        return isInLine(new Coordinates(endObj.getX(), endObj.getY()), new Coordinates(testObj.getX(), testObj.getY()), DEFAULT_ERROR_LINE);
    }

    /**
     * Test whether an object in the arena is within a certain error of a line defined by this point and another point, extending towards infinity.
     * The default allowable error is {@value #DEFAULT_ERROR_LINE} radians.
     * @param endPt The other point of the line, which should not be at the same coordinates as this object.
     * @param testObj The object in the arena to be tested.
     * @return Whether the test object is within the specified error of the line.
     */
    public boolean isInLine(@NonNull Coordinates endPt, @NonNull ExistsInArena testObj) {
        return isInLine(endPt, new Coordinates(testObj.getX(), testObj.getY()), DEFAULT_ERROR_LINE);
    }

    /**
     * Test whether a point is within a certain error of a line defined by this point and another point, extending towards infinity.
     * The default allowable error is {@value #DEFAULT_ERROR_LINE} radians.
     * @param endPt The other point of the line, which should not be at the same coordinates as this object.
     * @param testPt The point to be tested.
     * @return Whether the test point is within the specified error of the line.
     */
    public boolean isInLine(@NonNull Coordinates endPt, @NonNull Coordinates testPt) {
        return isInLine(endPt, testPt, DEFAULT_ERROR_LINE);
    }

    /**
     * Test whether a point is within a certain error of a line defined by this point and another point, extending towards infinity.
     * @param endPt The other point of the line, which should not be at the same coordinates as this object.
     * @param testPt The point to be tested.
     * @param error Allowable error in terms of radians.
     * @return Whether the test point is within the specified error of the line.
     */
    public boolean isInLine(@NonNull Coordinates endPt, @NonNull Coordinates testPt, double error) {
        if (endPt.isAt(this)) throw new InvalidParameterException("Parameter endPt cannot be at the same coordinates as this object");

        if (testPt.isAt(this) || testPt.isAt(endPt)) return true;

        return Math.abs(this.angleFrom(endPt)-this.angleFrom(testPt)) < error;
    }

    public boolean isInCircle(@NonNull ExistsInArena obj, int radius) {
        return isInCircle(new Coordinates(obj.getX(), obj.getY()),radius);
    }

    /**
     * Test whether a point is within a circle of current point as the center
     * @param coordinate The coordinate which to be tested
     * @param radius the radius of the circle with current point as the center
     * @return True if the tested coordinate is in the circle, otherwise false
     */
    public boolean isInCircle(@NonNull Coordinates coordinate, int radius) {
        return this.diagonalDistanceFrom(coordinate) < radius;
    }

    public LinkedList<Monster> monsterInCircle(@NonNull Coordinates coordinate, int radius){
        LinkedList<Monster> monsters = new LinkedList<>();
        for (Monster m :Arena.getMonsters()) {
            if (isInCircle(m,radius))monsters.add(m);
        }
        return monsters;
    }

    public Coordinates findEdgePt(@NonNull ExistsInArena dirObj) {
        return findEdgePt(new Coordinates(dirObj.getX(), dirObj.getY()));
    }

    /**
     * Find the point in the edge which is in the line of the current point and given point
     * @param dirPt The point will form a extended line with the current point
     * @return  The point in the edge of the extended line
     */
    public Coordinates findEdgePt(@NonNull Coordinates dirPt){
        for (int y = 0; y <= UIController.ARENA_WIDTH; ++y){
            if(this.isInLine(dirPt,new Coordinates(0,y)))
                return new Coordinates(0,y);
            if(this.isInLine(dirPt,new Coordinates(UIController.ARENA_WIDTH,y)))
                return new Coordinates(UIController.ARENA_WIDTH,y);
        }
        for (int x = 0; x <= UIController.ARENA_HEIGHT; ++x){
            if(this.isInLine(dirPt,new Coordinates(x,0)))
                return new Coordinates(x,0);
            if(this.isInLine(dirPt,new Coordinates(x,UIController.ARENA_HEIGHT)))
                return new Coordinates(x,UIController.ARENA_HEIGHT);
        }
        return dirPt;//for ignoring warning

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
        Line line = new Line(this.x,this.y,cor.x,cor.y);//ui part incomplete
    }
}