package project.entity;

import javax.persistence.*;

import org.checkerframework.checker.nullness.qual.Nullable;

import javafx.scene.image.ImageView;
import project.arena.ArenaEventRegister;
import project.control.ArenaManager;
import project.event.EventHandler;
import project.event.eventargs.ArenaObjectEventArgs;
import project.event.eventargs.EventArgs;
import project.query.ArenaObjectStorage;

/**
 * Represents an object that exists in an {@link ArenaObjectStorage}.
 */
@Entity(name="ArenaObject")
public abstract class ArenaObject {

    /**
     * ID for storage using Java Persistence API
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The storage where the object is located within.
     */
    @ManyToOne
    protected ArenaObjectStorage storage;

    /**
     * The ImageView that the object is bound to.
     */
    @Transient
    protected ImageView imageView = getDefaultImage();

    /**
     * The position of the object within the storage.
     */
    @OneToOne(cascade = {CascadeType.ALL})
    protected ArenaObjectPositionInfo positionInfo;

    /**
     * The method invoked when the next frame is called.
     */
    @Transient
    @Nullable
    protected EventHandler<EventArgs> onNextFrame = null;

    /**
     * Default constructor.
     */
    public ArenaObject() {}

    /**
     * Constructs a newly allocated {@link ArenaObject} object.
     * @param x The x-coordinate of the object within the storage.
     * @param y The y-coordinate of the object within the storage.
     */
    public ArenaObject(short x, short y) {
        this.storage = ArenaManager.getActiveObjectStorage();
        this.positionInfo = new ArenaObjectPositionInfo(imageView, x, y);
    }

    /**
     * Subscribes the object to each event, only meant to be called by {@link ArenaObjectFactory}.
     * @return <code>true</code> iff the object was not originally subscribed to each event.
     */
    boolean subscribeEvents() {
        boolean success = true;

        if (onNextFrame != null) {
            success = success && ArenaManager.getActiveEventRegister().ARENA_NEXT_FRAME.subscribe(onNextFrame);
        }

        return success;
    }
    /**
     * Unsubscribes the object from each event only meant to be called by {@link ArenaObjectFactory}.
     * @return <code>true</code> iff the object was originally subscribed to each event.
     */
    boolean unsubscribeEvents() {
        boolean success = true;

        if (onNextFrame != null) {
            success = success && ArenaManager.getActiveEventRegister().ARENA_NEXT_FRAME.unsubscribe(onNextFrame);
        }

        return success;
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
     * while also updating the position of the bound ImageView, on the mover's behalf.
     * @param mover The object which moves this object.
     * @param x The x-coordinate of the new position.
     * @param y The y-coordinate of the new position.
     * @throws IllegalArgumentException If the position is out of bounds.
     */
    public void moveObject(Object mover, short x, short y) throws IllegalArgumentException {
        ArenaEventRegister register = ArenaManager.getActiveEventRegister();

        register.ARENA_OBJECT_MOVE_START.invoke(mover,
                new ArenaObjectEventArgs() {
                    { subject = ArenaObject.this; }
                }
        );

        this.positionInfo.setPosition(x, y);

        register.ARENA_OBJECT_MOVE_END.invoke(mover,
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
    protected void loadArenaObject() {
        this.positionInfo = new ArenaObjectPositionInfo(imageView, getX(), getY());
    }

    public ArenaObjectPositionInfo getPositionInfo() {
        return positionInfo;
    }
}