package project.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import project.control.ArenaManager;
import project.util.Geometry;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

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
                damageTarget();

                // Remove projectile from arena once it has reached target
                dispose(this);
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
     * Default constructor.
     */
    public Projectile() {}

    /**
     * Constructs a newly allocated {@link Projectile} object and adds it to the currently active arena.
     * @param tower The tower from which this projectile originates.
     * @param target The monster that the projectile will pursue.
     * @param deltaX The x-offset from the targeted monster where the projectile will land.
     * @param deltaY The y-offset from the targeted monster where the projectile will land.
     */
    public Projectile(Tower tower, Monster target, short deltaX, short deltaY) {
        super(tower.getX(), tower.getY());
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
    public double getDamage() { return damage; }

    /**
     * Returns the speed of the projectile.
     * @return The speed of the projectile.
     */
    public double getSpeed() { return speed; }

    /**
     * Damages the target, and possibly other {@link Monster}s.
     */
    protected void damageTarget() {
        target.takeDamage(damage, this);
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