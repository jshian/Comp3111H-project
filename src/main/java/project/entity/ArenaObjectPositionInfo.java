package project.entity;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.image.ImageView;
import project.control.ArenaManager;

import javax.persistence.*;

/**
 * Represents the position of an {@link ArenaObject}.
 */
@Entity
@Access(AccessType.PROPERTY)
public final class ArenaObjectPositionInfo {

    /**
     * ID for storage using Java Persistence API
     */
    private Integer id;
    private IntegerProperty x = new SimpleIntegerProperty();
    private IntegerProperty y = new SimpleIntegerProperty();

    /**
     * Constructs a newly allocated {@link ArenaObjectPositionInfo} object.
     * @param imageView The ImageView to bound the object's position to.
     * @param x The x-coordinate to store.
     * @param y The y-coordinate to store.
     * @throws IllegalArgumentException If the position is out of bounds.
     */
    ArenaObjectPositionInfo(ImageView imageView, short x, short y) throws IllegalArgumentException {
        setPosition(x, y);

        imageView.xProperty().bind(Bindings.add(this.x, - imageView.getImage().getWidth() / 2));
        imageView.yProperty().bind(Bindings.add(this.y, - imageView.getImage().getHeight() / 2));
    }

    /**
     * Sets the position of the object within the same storage,
     * while also updating the position of the bound ImageView.
     * @param x The x-coordinate of the new position.
     * @param y The y-coordinate of the new position.
     * @throws IllegalArgumentException If the position is out of bounds.
     */
    void setPosition(short x, short y) throws IllegalArgumentException {
        assertValidPosition(x, y);

        this.x.set(x);
        this.y.set(y);
    }

    /**
     * Performs bounds checking of the position.
     * @param x The x-coordinate of the position.
     * @param y The y-coordinate of the position.
     * @throws IllegalArgumentException If the coordinates is out of bounds.
     */
    public static void assertValidPosition(short x, short y) throws IllegalArgumentException {
        if (x < 0 || x > ArenaManager.ARENA_WIDTH) {
            throw new IllegalArgumentException(String.format("The parameter 'x' is out of bounds."));
        }

        if (y < 0 || y > ArenaManager.ARENA_HEIGHT) {
            throw new IllegalArgumentException(String.format("The parameter 'y' is out of bounds."));
        }
    }

    /**
     * Returns the x-coordinate of the object.
     * @return The x-coordinate of the object.
     */
    @Column(name = "x")
    public short getX() {
        return (short) x.get();
    }

    /**
     * Returns the y-coordinate of the object.
     * @return The y-coordinate of the object.
     */
    @Column(name = "y")
    public short getY() {
        return (short) y.get();
    }

    /**
     * Returns the id of the object.
     * @return The id of the object.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public int getId() {
        return id;
    }

    // setters below is used for hibernate.
    /**
     * Sets the id of the object within the same storage.
     * @param id The id of the object.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Sets the x-coordinate of the object within the same storage,
     * while also updating the position of the bound ImageView.
     * @param x x-coordinate of the object.
     */
    public void setX(int x) {
        assertValidPosition((short)x, (short)1);
        this.x.set(x);
    }

    /**
     * Sets the y-coordinate of the object within the same storage,
     * while also updating the position of the bound ImageView.
     * @param y y-coordinate of the object.
     */
    public void setY(int y) {
        assertValidPosition((short)1, (short)y);
        this.y.set(y);
    }
}