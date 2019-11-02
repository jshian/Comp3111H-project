package project;

import java.security.InvalidParameterException;
import java.util.*;

import javafx.scene.shape.Line;
import org.apache.commons.lang3.*;
import sun.font.CoreMetrics;

import project.Arena.TypeFilter;

/**
 * Custom class to store 2D Cartesian coordinates of objects in the arena. Also
 * stores a link to the arena itself.
 * @see Arena
 */
public class Coordinates {

    private Arena arena;
    
    private int x = 0;
    private int y = 0;

    /**
     * Constructor for the Coordinate class. Both coordinates default to 0.
     * @param arena The arena that the Coordinate is linked to.
     */
    public Coordinates(Arena arena) { this.arena = arena; }

    /**
     * Parametized constructor for the Coordinate class.
     * @param arena The arena that the Coordinate is linked to.
     * @param x The horizontal coordinate, increasing towards the right.
     * @param y The vertical coordinate, increasing towards the bottom.
     * @exception InvalidParameterException Either of the coordinates is outside the arena.
     */
    public Coordinates(Arena arena, int x, int y) { this.arena = arena; update(x, y); }

    /**
     * Finds all objects that occupy the specified coordinate in the arena.
     * @param coordinates The specified coordinates.
     * @return A linked list containing a reference to each object that satisfy the above criteria. Note that they do not have to be located at said coordinate.
     */
    public LinkedList<Object> findObjectsOccupying(Coordinates coordinates)
    {
        LinkedList<Object> result = new LinkedList<>();

        result.addAll(arena.objectsAtPixel(this, EnumSet.of(TypeFilter.Projectile, TypeFilter.Monster)));
        result.addAll(arena.objectsInGrid(this, EnumSet.of(TypeFilter.Tower)));
        
        return result;
    }

    /**
     * Accesses the Arena this object is linked to.
     * @return The Arena this object is linked to.
     */
    public Arena getArena() { return arena; }

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
     * Determines whether two Coordinate objects represent the same Cartesian coordinates.
     * @param other The other object.
     * @return Whether the two Coordinate objects represent the same Cartesian coordinates.
     */
    public boolean isAt(Coordinates other) {
        return ((int) diagonalDistanceFrom(other)) == 0;
    }

    /**
     * Calculates the taxicab distance between the Cartesian coordinates represented by two Coordinate objects.
     * @param other The other object.
     * @return The taxicab distance between the Cartesian coordinates represented by the two Coordinate objects.
     */
    public int taxicabDistanceFrom(Coordinates other) {
        return this.x - other.x + this.y - other.y;
    }

    /**
     * Calculates the diagonal distance between the Cartesian coordinates represented by two Coordinate objects.
     * @param other The other object.
     * @return The diagonal distance between the Cartesian coordinates represented by the two Coordinate objects.
     */
    public double diagonalDistanceFrom(Coordinates other) {
        return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2));
    }

    /**
     * Calculates the angle between the coordinates represented by two Coordinate objects.
     * @param other The other object.
     * @return The angle in radians from this object to the other object, as if this object is at the origin of a polar coordinate system.
     */
    public double angleFrom(Coordinates other) {
        return Math.atan2(other.y - this.y, other.x - this.x);
    }

    /**
     * Test whether the point in the line or not within a small error accepted
     * The line may be expanded
     * @param endPt The point in the end of the Line
     * @param testPt The point to be tested
     * @return Is the test point in the line or not
     */
    public boolean isInLine(Coordinates endPt, Coordinates testPt) {
        double error = 0.02; //0.02 radians around 1 degree
        return Math.abs(this.angleFrom(endPt)-this.angleFrom(testPt)) < error;
    }

    //dont know how to implement
    public boolean isInArea(Coordinates endPt, Coordinates testPt) {
        return false;
    }

    //problematic: the size of arena is unknown
    public Coordinates findEdgePt(Coordinates dirPt){
        for (int row = 0; row <= 440; ++row){
            if(this.isInLine(dirPt,new Coordinates(this.arena,0,row)))
                return new Coordinates(this.arena,0,row);
            if(this.isInLine(dirPt,new Coordinates(this.arena,440,row)))
                return new Coordinates(this.arena,440,row);
        }
        for (int col = 0; col <= 440; ++col){
            if(this.isInLine(dirPt,new Coordinates(this.arena,col,0)))
                return new Coordinates(this.arena,col,0);
            if(this.isInLine(dirPt,new Coordinates(this.arena,col,440)))
                return new Coordinates(this.arena,col,440);
        }
        return dirPt;//for ignoring warning

    }

    /**
     * Draw a line from the laser tower to certain position.
     * @param cor The coordinate of the target.
     */
    public void drawLine(Coordinates cor){
        Line line = new Line(this.x,this.y,cor.x,cor.y);//ui part incomplete
    }
}