package project.entity;

import javax.persistence.Entity;
import javafx.scene.image.ImageView;
import project.controller.ArenaEventManager;
import project.event.EventHandler;
import project.event.eventargs.EventArgs;
import project.event.eventsets.ArenaEvent;
import project.query.ArenaObjectStorage;

/**
 * Represents an object that exists in an {@link ArenaObjectStorage}.
 */
@Entity
public abstract class ArenaObject {

    /**
     * The storage where the object is located within.
     */
    protected ArenaObjectStorage storage;

    /**
     * The ImageView that the object is bound to.
     */
    protected ImageView imageView;

    /**
     * The position of the object within the storage.
     */
    protected ArenaObjectPositionInfo position;

    /**
     * The method invoked when the next frame is called.
     */
    protected EventHandler<EventArgs> onNextFrame;
    
    /**
     * Constructs a newly allocated ArenaObject object.
     * @param storage The storage to add the object to.
     * @param imageView The ImageView to bound the object to.
     * @param x The x-coordinate of the object within the storage.
     * @param y The y-coordinate of the object within the storage.
     */
    public ArenaObject(ArenaObjectStorage storage, ImageView imageView, short x, short y) {
        this.storage = storage;
        this.imageView = imageView;
        this.position = new ArenaObjectPositionInfo(imageView, x, y);

        if (onNextFrame != null) ArenaEventManager.ARENA.subscribe(ArenaEvent.NEXT_FRAME, onNextFrame);
    }

    /**
     * Returns the ImageView that the object is bound to.
     * @return The ImageView that the object is bound to.
     */
    public final ImageView getImageView() { return imageView; }

    /**
     * Returns the x-coordinate of the object within the storage.
     * @return The x-coordinate of the object within the storage.
     */
    public final short getX() { return position.getX(); }

    /**
     * Returns the y-coordinate of the object within the storage.
     * @return The y-coordinate of the object within the storage.
     */
    public final short getY() { return position.getY(); }

    /**
     * Updates the position of the object within the same storage,
     * while also updating the position of the bound ImageView.
     * @param x The x-coordinate of the new position.
     * @param y The y-coordinate of the new position.
     * @throws IllegalArgumentException If the position is out of bounds.
     */
    protected final void updatePosition(short x, short y) throws IllegalArgumentException {
        this.position.setPosition(x, y);
    }
}