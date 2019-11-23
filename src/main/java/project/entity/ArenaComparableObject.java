package project.entity;

import javafx.scene.image.ImageView;
import project.query.ArenaObjectStorage;

/**
 * An {@link ArenaObject} that implements the {@link Comparable} interface.
 */
public abstract class ArenaComparableObject extends ArenaObject implements Comparable<ArenaComparableObject> {

    /**
     * Constructs a newly allocated {@link ArenaComparableObject} object.
     * @param storage The storage to add the object to.
     * @param imageView The ImageView to bound the object to.
     * @param x The x-coordinate of the object within the storage.
     * @param y The y-coordinate of the object within the storage.
     */
    public ArenaComparableObject(ArenaObjectStorage storage, ImageView imageView, short x, short y) {
        super(storage, imageView, x, y);
    }

    public int compareTo(ArenaComparableObject o) {
        return 0; // By default, objects of different types are considered "equal"
    }

}