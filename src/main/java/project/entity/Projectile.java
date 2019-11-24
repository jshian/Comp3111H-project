package project.entity;

import javafx.scene.image.ImageView;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import project.Geometry;
import project.controller.ArenaManager;
import project.event.eventargs.ArenaObjectEventArgs;
import project.query.ArenaObjectStorage;

/**
 * Projectiles are shot by a {@link Tower} towards a {@link Monster} and deal damage on contact. They disappear when they reach their target.
 * Projectiles do not have collision boxes, thus multiple of them can exist on the same pixel.
 */
@Entity
public abstract class Projectile extends ArenaObject implements ObjectWithTarget {
    /**
     * ID for storage using Java Persistence API
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    /**
     * Represents the position from which the projectile is fired.
     */
    @NotNull
    @OneToOne
    protected Tower origin;

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
     * The damage dealt by the projectile.
     */
    protected double damage = 1;

    /**
     * The maximum number of pixels the projectile can travel per frame.
     * Projectiles can travel diagonally.
     */
    protected double speed = 1;

    /**
     * The non-integral portion of the movement during each frame is accumulated here.
     * When it reaches one, it is consumed to allow the projectile to move an extra pixel.
     */
    protected double unusedMovement = 0;

    /**
     * Whether the projectile has reached the target location.
     */
    protected boolean hasReachedTarget = false;

    // Define onNextFrame before constructor
    {
        onNextFrame = (sender, args) -> {
            short targetX, targetY;
            targetX = (short) (target.getX() + deltaX);
            if (targetX < 0) targetX = 0;
            else if (targetX > ArenaManager.ARENA_WIDTH) targetX = ArenaManager.ARENA_WIDTH;
            
            targetY = (short) (target.getY() + deltaY);
            if (targetY < 0) targetY = 0;
            else if (targetY > ArenaManager.ARENA_HEIGHT) targetY = ArenaManager.ARENA_HEIGHT;
    
            double potentialDistanceTravelled = speed + unusedMovement;
    
            if (getMovementDistanceToDestination() <= potentialDistanceTravelled) {
                updatePosition(targetX, targetY);
                hasReachedTarget = true;
                damageTarget();

                // Remove projectile from arena once it has reached target
                ArenaManager.getActiveEventRegister().ARENA_OBJECT_REMOVE.invoke(this,
                        new ArenaObjectEventArgs() {
                            { subject = Projectile.this; }
                        }
                );
            } else {
                double angleFromTarget = Geometry.findAngleFrom(getX(), getY(), targetX, targetY);
                short newX = (short) (getX() + (short) (potentialDistanceTravelled * Math.cos(angleFromTarget)));
                short newY = (short) (getY() + (short) (potentialDistanceTravelled * Math.sin(angleFromTarget)));
    
                double actualDistanceTravelled = Geometry.findEuclideanDistance(getX(), getY(), newX, newY);
                updatePosition(newX, newY);
                unusedMovement += speed - actualDistanceTravelled;
            }
        };
    }

    /**
     * Constructs a newly allocated {@link Projjectile} object and adds it to the {@link ArenaObjectStorage}.
     * @param storage The storage to add the object to.
     * @param imageView The ImageView to bound the object to.
     * @param tower The tower from which this projectile originates.
     * @param target The monster that the projectile will pursue.
     * @param deltaX The x-offset from the targeted monster where the projectile will land.
     * @param deltaY The y-offset from the targeted monster where the projectile will land.
     */
    public Projectile(ArenaObjectStorage storage, ImageView imageView, Tower tower, Monster target, short deltaX, short deltaY) {
        super(storage, imageView, tower.getX(), tower.getY());
        this.target = target;
        this.deltaX = deltaX;
        this.deltaY = deltaY;
        this.speed = tower.getProjectileSpeed();
        this.damage = tower.getAttackPower();

        System.out.println(String.format("%s@(%d, %d) -> %s@(%d, %d)",
                tower.getDisplayName(), origin.getX(), origin.getY(), 
                target.getDisplayName(), target.getX(), target.getY()));
    }

    /**
     * Returns the damage dealt by the projectile.
     * @return The damage dealt by the projectile.
     */
    public final double getDamage() { return damage; }

    /**
     * Returns the speed of the projectile.
     * @return The speed of the projectile.
     */
    public final double getSpeed() { return speed; }

    /**
     * Damages the target, and possibly other {@link Monster}s.
     */
    protected void damageTarget() {
        target.takeDamage(damage);
    }

    @Override
    public short getTargetLocationX() { return target.getX(); }

    @Override
    public short getTargetLocationY() { return target.getY(); }

    @Override
    public double getMovementDistanceToDestination() {
        return Geometry.findEuclideanDistance(getX(), getY(), target.getX(), target.getY());
    }
}