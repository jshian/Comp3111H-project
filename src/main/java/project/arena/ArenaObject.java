package project.arena;

import org.checkerframework.checker.nullness.qual.NonNull;

import javafx.scene.image.ImageView;

/**
 * Interface for objects that exist inside the {@link Arena}.
 */
public interface ArenaObject {
    /**
     * Access the ImageView associated with the object.
     * @return The ImageView associated with the object.
     */
    ImageView getImageView();

    /**
     * Accesses the x-coordinate of the object.
     * @return The x-coordinate of the object.
     * @see Coordinates
     */
    short getX();

    /**
     * Accesses the y-coordinate of the object.
     * @return The y-coordinate of the object.
     * @see Coordinates
     */
    short getY();

    /**
     * Updates the {@link Coordinates} of the object.
     * @param x The new x-coordinate.
     * @param y The new y-coordinate.
     */
    void setLocation(short x, short y);

    /**
     * Updates the coordinates of the object.
     * @param coordinates The new coordinates.
     */
    void setLocation(@NonNull Coordinates coordinates);

    /**
     * Updates the object by one frame.
     */
     void nextFrame();

    /**
     * Load the ImageView of the object.
     */
    void loadImage();
}