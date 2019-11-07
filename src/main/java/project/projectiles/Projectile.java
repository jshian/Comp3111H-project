package project.projectiles;

import project.Arena;
import project.Coordinates;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.apache.commons.lang3.*;

import javafx.scene.image.ImageView;

/**
 * Projectiles are shot by a Tower towards Monsters and deal damage on contact. They disappear when they reach their target.
 * Projectiles do not have collision boxes, thus multiple of them can exist on the same pixel.
 */
@Entity
public class Projectile implements Arena.MovesInArena {
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
     * The Arena that this projectile is attached to.
     */
    @Transient
    protected final Arena arena;
    
    /**
     * Represents the position of the projectile.
     */
    @NotNull
    private Coordinates coordinates;

    /**
     * The coordinate of Monster that the projectile is travelling towards.
     */
    @NotNull
    private Coordinates target;

    /**
     * The maximum number of pixels the projectile can travel per frame.
     * Projectiles can travel diagonally.
     */
    @NotNull
    private double speed;

    /**
     * The current attack power of the projectile.
     */
    @NotNull
    private int attackPower;

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
        this.coordinates = coordinates;
        this.target = target;
        this.speed = speed;
        this.attackPower = attackPower;
    }

    // Inferface implementation
    public ImageView getImageView() { return imageView; }
    public int getX() { return coordinates.getX(); }
    public int getY() { return coordinates.getY(); }
    public void refreshDisplay() { throw new NotImplementedException("TODO"); }
    public void setLocation(int x, int y) { coordinates = new Coordinates(x, y); }
    public void MoveOneFrame() {
        double distance = coordinates.diagonalDistanceFrom(target);

        if (distance <= speed)
            coordinates.update(target.getX(), target.getY());
        else {
            int newX = coordinates.getX() + (int) (speed * Math.cos(coordinates.angleFrom(target)));
            int newY = coordinates.getY() + (int) (speed * Math.sin(coordinates.angleFrom(target)));
            coordinates.update(newX, newY);
        }
    }

    /**
     * Determines whether the projectile has reached its target.
     * @return Whether the projectile has reached its target.
     */
    public boolean hasReachedTarget() { return coordinates.isAt(target); }
}