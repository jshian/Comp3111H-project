package project.entity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.checkerframework.checker.nullness.qual.Nullable;

import javafx.scene.image.ImageView;
import project.controller.ArenaEventRegister;
import project.controller.ArenaManager;
import project.event.EventHandler;
import project.event.eventargs.ArenaObjectEventArgs;
import project.event.eventargs.EventArgs;
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
    protected ArenaObjectPositionInfo positionInfo;

    /**
     * The method invoked when the next frame is called.
     */
    @Nullable
    protected EventHandler<EventArgs> onNextFrame = null;
    
    /**
     * Constructs a newly allocated {@link ArenaObject} object.
     * 
     * Automatically broadcasts the {@link ArenaEventRegister#ARENA_OBJECT_ADD} event.
     * 
     * @param storage The storage to add the object to.
     * @param imageView The ImageView to bound the object to.
     * @param x The x-coordinate of the object within the storage.
     * @param y The y-coordinate of the object within the storage.
     */
    public ArenaObject(ArenaObjectStorage storage, ImageView imageView, short x, short y) {
        this.storage = storage;
        this.imageView = imageView;
        this.positionInfo = new ArenaObjectPositionInfo(imageView, x, y);

        ArenaManager.getActiveEventRegister().ARENA_OBJECT_ADD.invoke(this,
                new ArenaObjectEventArgs() {
                    { subject = ArenaObject.this; }
                }
        );

        if (onNextFrame != null) {
            ArenaManager.getActiveEventRegister().ARENA_NEXT_FRAME.subscribe(onNextFrame);
        }
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
    public final short getX() { return positionInfo.getX(); }

    /**
     * Returns the y-coordinate of the object within the storage.
     * @return The y-coordinate of the object within the storage.
     */
    public final short getY() { return positionInfo.getY(); }

    /**
     * Updates the position of the object within the same storage,
     * while also updating the position of the bound ImageView.
     * 
     * Automatically broadcasts the {@link ArenaEventRegister#ARENA_OBJECT_MOVE_START}
     * and {@link ArenaEventRegister#ARENA_OBJECT_MOVE_END} events.
     * 
     * @param x The x-coordinate of the new position.
     * @param y The y-coordinate of the new position.
     * @throws IllegalArgumentException If the position is out of bounds.
     */
    public final void updatePosition(short x, short y) throws IllegalArgumentException {
        ArenaEventRegister register = ArenaManager.getActiveEventRegister();

        register.ARENA_OBJECT_MOVE_START.invoke(this,
                new ArenaObjectEventArgs() {
                    { subject = ArenaObject.this; }
                }
        );

        this.positionInfo.setPosition(x, y);

        register.ARENA_OBJECT_MOVE_END.invoke(this,
                new ArenaObjectEventArgs() {
                    { subject = ArenaObject.this; }
                }
        );
    }
}