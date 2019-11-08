package project.projectiles;

import javafx.scene.image.Image;
import project.*;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.checkerframework.checker.nullness.qual.NonNull;

import javafx.scene.image.ImageView;
import project.towers.Tower;

import java.util.EnumSet;
import java.util.LinkedList;

/**
 * Projectiles are shot by a Tower towards Monsters and deal damage on contact. They disappear when they reach their target.
 * Projectiles do not have collision boxes, thus multiple of them can exist on the same pixel.
 */
@Entity
public abstract class Projectile implements Arena.MovesInArena {
    /**
     * ID for storage using Java Persistence API
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    /**
     * The ImageView that displays the projectile.
     */
    @Transient
    private ImageView imageView;

    /**
     * The tower that create this projectile.
     */
    @Transient
    protected Tower tower;

    /**
     * The Arena that this projectile is attached to.
     */
    @Transient
    protected final Arena arena;

    /**
     * Represents the position of the projectile.
     */
    @NotNull
    protected Coordinates coordinates;

    /**
     * The coordinate of Monster that the projectile is travelling towards.
     */
    @NotNull
    protected Coordinates target;

    /**
     * The maximum number of pixels the projectile can travel per frame.
     * Projectiles can travel diagonally.
     */
    @NotNull
    protected double speed;

    /**
     * The current attack power of the projectile.
     */
    @NotNull
    protected int attackPower;

    /**
     * Constructor for the Projectile class.
     * @param arena The arena the projectile is attached to.
     * @param coordinates The coordinates of the pixel where the projectile is initially located.
     * @param target The Monster that the projectile will pursue, which should not be <code>null</code>.
     * @param speed The speed of the projectile.
     * @param attackPower The attack power of the projectile.
     */
    public Projectile(Arena arena, Coordinates coordinates, Coordinates target, double speed, int attackPower) {
        this.arena = arena;
        this.coordinates = Grid.findGridCenter(coordinates);
        this.target = target;
        this.speed = speed;
        this.attackPower = attackPower;
        this.imageView = new ImageView(new Image("/projectile.png", UIController.GRID_WIDTH/4,
                UIController.GRID_HEIGHT/4, true, true));
        this.coordinates.bindByImage(this.imageView);
        LinkedList<Arena.ExistsInArena> l = arena.findObjectsInGrid(Grid.findGridCenter(coordinates), EnumSet.of(Arena.TypeFilter.Tower));
        this.tower = (Tower)l.peek();
    }

    /**
     * Copy constructor for the Projectile class. Performs deep copy.
     * @param other The other object to copy form.
     */
    public Projectile(Projectile other){
        this.imageView = new ImageView(other.imageView.getImage());
        this.arena = other.arena;
        this.coordinates = new Coordinates(other.coordinates);
        this.target = new Coordinates(other.target);
        this.speed = other.speed;
        this.attackPower = other.attackPower;
        this.coordinates.bindByImage(this.imageView);
        this.tower = other.tower;
    }

    /**
     * Creates a deep copy of the projectile.
     * @return A deep copy of the projectile.
     */
    public abstract Projectile deepCopy();

    // Inferface implementation
    public ImageView getImageView() { return imageView; }
    public int getX() { return coordinates.getX(); }
    public int getY() { return coordinates.getY(); }
    public void setLocation(int x, int y) { this.coordinates.update(x, y); }
    public void setLocation(@NonNull Coordinates coordinates) { this.coordinates.update(coordinates); }
    public void moveOneFrame() {
        double distance = Geometry.findEuclideanDistance(getX(), getY(), target.getX(), target.getY());

        if (distance <= speed)
            coordinates.update(target.getX(), target.getY());
        else {
            double angleFromTarget = Geometry.findAngleFrom(getX(), getY(), target.getX(), target.getY());
            int newX = coordinates.getX() + (int) (speed * Math.cos(angleFromTarget));
            int newY = coordinates.getY() + (int) (speed * Math.sin(angleFromTarget));
            coordinates.update(newX, newY);
        }


    }

    /**
     * Determines whether the projectile has reached its target.
     * @return Whether the projectile has reached its target.
     */
    public boolean hasReachedTarget() { return Geometry.isAt(getX(), getY(), target.getX(), target.getY()); }


    /**
     * Accesses the tower that throws the current projectile.
     * @return The tower that throws the current projectile.
     */
    public Tower getTower() { return this.tower; }

    /**
     * Accesses the attack power of the current projectile.
     * @return The attack power of the current projectile.
     */
    public int getAttackPower() { return attackPower; }
}