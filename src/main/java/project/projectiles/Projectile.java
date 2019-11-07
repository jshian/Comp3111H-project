package project.projectiles;

import javafx.scene.image.Image;
import project.Arena;
import project.Coordinates;
import project.Grid;
import project.UIController;
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
     * @param coordinates The coordinates of the pixel where the projectile is initially located.
     * @param target The Monster that the projectile will pursue, which should not be <code>null</code>.
     * @param speed The speed of the projectile.
     * @param attackPower The attack power of the projectile.
     */
    public Projectile(Coordinates coordinates, Coordinates target, double speed, int attackPower) {
        this.coordinates = Grid.findGridCenter(coordinates);
        this.target = target;
        this.speed = speed;
        this.attackPower = attackPower;
        this.imageView = new ImageView(new Image("/projectile.png", UIController.GRID_WIDTH/4,
                UIController.GRID_HEIGHT/4, true, true));
        this.coordinates.bindByImage(this.imageView);
    }

    // Inferface implementation
    public ImageView getImageView() { return imageView; }
    public int getX() { return coordinates.getX(); }
    public int getY() { return coordinates.getY(); }
    public int getAttackPower() { return attackPower; }
    public void setLocation(int x, int y) { coordinates.update(new Coordinates(x, y)); }
    public void moveOneFrame() {
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