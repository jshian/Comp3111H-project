package project;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Point2D;
import javafx.scene.image.ImageView;
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
     * @exception IllegalArgumentException Either of the coordinates is outside the arena.
     */
    public void update(int x, int y) {
        if (x < 0 || x > UIController.ARENA_WIDTH)
            throw new IllegalArgumentException(
                String.format("The parameter 'x' is out of bounds. It should be between 0 and %d.", UIController.ARENA_WIDTH));
        if (y < 0 || y > UIController.ARENA_HEIGHT)
            throw new IllegalArgumentException(
                String.format("The parameter 'y' is out of bounds. It should be between 0 and %d.", UIController.ARENA_HEIGHT));

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
}