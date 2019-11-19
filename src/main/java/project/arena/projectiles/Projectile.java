package project.arena.projectiles;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.checkerframework.checker.nullness.qual.NonNull;

import javafx.scene.image.ImageView;
import project.Geometry;
import project.UIController;
import project.arena.Arena;
import project.arena.Coordinates;
import project.arena.ArenaMovingObject;
import project.arena.monsters.Monster;
import project.arena.towers.Tower;

/**
 * Projectiles are shot by a Tower towards Monsters and deal damage on contact. They disappear when they reach their target.
 * Projectiles do not have collision boxes, thus multiple of them can exist on the same pixel.
 */
@Entity
public abstract class Projectile implements ArenaMovingObject {
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
    protected ImageView imageView;

    /**
     * The Arena that this projectile is attached to.
     */
    @NotNull
    @ManyToOne
    protected final Arena arena;

    /**
     * Represents the position of the projectile.
     */
    @NotNull
    @OneToOne
    protected Coordinates coordinates;

    /**
     * Represents the position from which the projectile is fired.
     */
    @NotNull
    @OneToOne
    protected Coordinates origin;

    /**
     * The monster that the projectile is travelling towards.
     */
    @NotNull
    @ManyToOne
    protected Monster target;

    /**
     * The offset in x-coordinate of the projectile's landing spot from the target monster.
     * @see Coordinates
     */
    protected short deltaX = 0;

    /**
     * The offset in y-coordinate of the projectile's landing spot from the target monster.
     * @see Coordinates
     */
    protected short deltaY = 0;

    /**
     * The non-integral portion of the movement during each frame is accumulated here.
     * When it reaches one, it is consumed to allow the projectile to move an extra pixel.
     */
    protected double unusedMovement = 0;

    /**
     * The maximum number of pixels the projectile can travel per frame.
     * Projectiles can travel diagonally.
     */
    protected double speed = 1;

    /**
     * Whether the projectile has reached the target location.
     */
    protected boolean hasReachedTarget = false;

    /**
     * The current attack power of the projectile.
     */
    protected int attackPower = 1;

    /**
     * Constructor for the Projectile class.
     * @param arena The arena the projectile is attached to.
     * @param tower The tower from which this projectile originates.
     * @param target The monster that the projectile will pursue.
     * @param deltaX The x-offset from the targeted monster where the projectile will land.
     * @param deltaY The y-offset from the targeted monster where the projectile will land.
     * @param imageView The ImageView that displays the projectile.
     */
    public Projectile(@NonNull Arena arena, @NonNull Tower tower, @NonNull Monster target, short deltaX, short deltaY, @NonNull ImageView imageView) {
        this.arena = arena;
        this.coordinates = new Coordinates(tower.getX(), tower.getY());
        this.origin = new Coordinates(tower.getX(), tower.getY());
        this.imageView = imageView;
        this.target = target;
        this.deltaX = deltaX;
        this.deltaY = deltaY;
        this.speed = tower.getProjectileSpeed();
        this.attackPower = tower.getAttackPower();

        this.coordinates.bindByImage(this.imageView);
    }

    /**
     * Constructor for making a copy of Projectile class that is linked to another arena and target monster.
     * @param arena The arena to link this object to.
     * @param target The target monster of the copy.
     * @param other The object to copy from.
     */
    public Projectile(@NonNull Arena arena, @NonNull Monster target, @NonNull Projectile other){
        this.imageView = new ImageView(other.imageView.getImage());
        this.arena = arena;
        this.coordinates = new Coordinates(other.coordinates);
        this.origin = new Coordinates(other.origin);
        this.target = target;
        this.deltaX = other.deltaX;
        this.deltaY = other.deltaY;
        this.unusedMovement = other.unusedMovement;
        this.speed = other.speed;
        this.hasReachedTarget = other.hasReachedTarget;
        this.attackPower = other.attackPower;
        
        this.coordinates.bindByImage(this.imageView);
    }

    /**
     * Creates a deep copy of the projectile while is linked to another arena and target monster.
     * @param arena The arena to link this object to.
     * @param target The target monster of the copy.
     * @return A deep copy of the projectile.
     */
    public abstract Projectile deepCopy(@NonNull Arena arena, @NonNull Monster target);

    // Interface implementation
    public final ImageView getImageView() { return imageView; }
    public final short getX() { return coordinates.getX(); }
    public final short getY() { return coordinates.getY(); }
    public final void setLocation(short x, short y) { this.coordinates.update(x, y); }
    public final void setLocation(@NonNull Coordinates coordinates) { this.coordinates.update(coordinates); }
    public final double getSpeed() { return speed; }
    public void nextFrame() {
        short targetX, targetY;
        targetX = (short) (target.getX() + deltaX);
        if (targetX < 0) targetX = 0;
        else if (targetX > UIController.ARENA_WIDTH) targetX = UIController.ARENA_WIDTH;
        
        targetY = (short) (target.getY() + deltaY);
        if (targetY < 0) targetY = 0;
        else if (targetY > UIController.ARENA_HEIGHT) targetY = UIController.ARENA_HEIGHT;

        double distanceFromTarget = Geometry.findEuclideanDistance(getX(), getY(), targetX, targetY);
        double potentialDistanceTravelled = speed + unusedMovement;

        if (distanceFromTarget <= potentialDistanceTravelled) {
            coordinates.update(targetX, targetY);
            hasReachedTarget = true;
            damageTarget();
        } else {
            double angleFromTarget = Geometry.findAngleFrom(getX(), getY(), targetX, targetY);
            short newX = (short) (getX() + (short) (potentialDistanceTravelled * Math.cos(angleFromTarget)));
            short newY = (short) (getY() + (short) (potentialDistanceTravelled * Math.sin(angleFromTarget)));

            double actualDistanceTravelled = Geometry.findEuclideanDistance(getX(), getY(), newX, newY);
            coordinates.update(newX, newY);
            unusedMovement += speed - actualDistanceTravelled;
        }
    }

    /**
     * Gets the class name of the tower that fired the projectile.
     * @return The class name of the tower that fired the projectile.
     */
    protected abstract String getTowerClassName();

    /**
     * Determines whether the projectile has reached its target.
     * @return Whether the projectile has reached its target.
     */
    public final boolean hasReachedTarget() { return hasReachedTarget; }

    /**
     * Damages the target of the projectile.
     */
    protected void damageTarget() {
        if (!target.hasDied()) {
            target.takeDamage(attackPower);
            System.out.println(String.format("%s@(%d, %d) -> %s@(%d, %d)", getTowerClassName(), origin.getX(), origin.getY()
                                , target.getClassName(), target.getX(), target.getY()));
        }
    }

    /**
     * Accesses the targeted monster of the projectile.
     * @return The targeted monster of the projectile.
     */
    public final Monster getTarget() { return target; }

    /**
     * Accesses the attack power of the projectile.
     * @return The attack power of the projectile.
     */
    public final int getAttackPower() { return attackPower; }
}