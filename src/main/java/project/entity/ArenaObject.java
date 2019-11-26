package project.entity;

import javax.persistence.*;

import org.checkerframework.checker.nullness.qual.Nullable;

import javafx.scene.image.ImageView;
import project.arena.ArenaEventRegister;
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
     * ID for storage using Java Persistence API
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * The storage where the object is located within.
     */
    protected ArenaObjectStorage storage;

    /**
     * The ImageView that the object is bound to.
     */
    protected ImageView imageView = getDefaultImage();

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
     * Constructs a newly allocated {@link ArenaObject} object and adds it to the {@link ArenaObjectStorage}.
     * @param storage The storage to add the object to.
     * @param x The x-coordinate of the object within the storage.
     * @param y The y-coordinate of the object within the storage.
     */
    public ArenaObject(ArenaObjectStorage storage, short x, short y) {
        this.storage = storage;
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
    public ImageView getImageView() { return imageView; }

    /**
     * Returns the x-coordinate of the object within the storage.
     * @return The x-coordinate of the object within the storage.
     */
    public short getX() { return positionInfo.getX(); }

    /**
     * Returns the y-coordinate of the object within the storage.
     * @return The y-coordinate of the object within the storage.
     */
    public short getY() { return positionInfo.getY(); }

    /**
     * Updates the position of the object within the same storage,
     * while also updating the position of the bound ImageView.
     * 
     * In addition, the {@link ArenaObjectStorage} is updated accordingly.
     * 
     * @param x The x-coordinate of the new position.
     * @param y The y-coordinate of the new position.
     * @throws IllegalArgumentException If the position is out of bounds.
     */
    public void updatePosition(short x, short y) throws IllegalArgumentException {
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

    /**
     * Returns the default image of the object.
     * @return The default image of the object.
     */
    protected abstract ImageView getDefaultImage();

    /**
     * Loads the image view when it is generated from the database.
     */
    @PostLoad
    protected void loadImage() {
        imageView = getDefaultImage();
        this.positionInfo = new ArenaObjectPositionInfo(imageView, getX(), getY());
    }
}