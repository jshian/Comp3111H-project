package project;

import project.monsters.*;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.apache.commons.lang3.*;

import javafx.scene.image.ImageView;

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
     * Represents the position of the projectile.
     */
    @NotNull
    private Coordinates coordinates;

    /**
     * The Monster that the projectile is travelling towards.
     */
    @NotNull
    private Monster target;

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
    protected int attackPower;

    /**
     * Constructor for the Projectile class.
     * @param coordinates The coordinates of the pixel where the projectile is initially located.
     * @param target The Monster that the projectile will pursue, which should not be <code>null</code>.
     * @param speed The speed of the projectile.
     * @param attackPower The attack power of the projectile.
     */
    public Projectile(Coordinates coordinates, Monster target, double speed, int attackPower) {
        this.coordinates = coordinates;
        this.target = target;
        this.speed = speed;
        this.attackPower = attackPower;
    }

    // Inferface implementation
    public ImageView getImageView() { return imageView; }
    public Coordinates getCoordinates() { return coordinates; }
    public void refreshDisplay() { throw new NotImplementedException("TODO"); }
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