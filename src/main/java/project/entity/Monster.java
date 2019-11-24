package project.entity;

import java.util.Iterator;
import java.util.LinkedList;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.image.ImageView;

import project.arena.Grid;
import project.controller.ArenaManager;
import project.event.eventargs.ArenaObjectEventArgs;
import project.field.ArenaScalarField;
import project.query.ArenaObjectStorage;

/**
 * Monsters spawn at the starting position and try to reach the end-zone of the arena.
 * They can only move horizontally or vertically towards an adjacent {@link Grid} that does not contain a Tower.
 * If they succeed, the game is lost.
 * Monsters do not have collision boxes, thus multiple of them can exist on the same pixel.
 */
@Entity
public abstract class Monster extends ArenaObject implements Comparable<Monster>, InformativeObject, ObjectWithTarget, ObjectWithTrail {

    /**
     * ID for storage using Java Persistence API
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    /**
     * The scalar field used to determine the movement of the monster via gradient descent.
     */
    @Transient
    protected ArenaScalarField<?> gradientDescentField = ArenaManager.getActiveScalarFieldRegister().MONSTER_DISTANCE_TO_END;

    /**
     * The current health of the monster. It cannot go beyond {@link #maxHealth}.
     * When this is not greater than zero, the monster is considered dead.
     * @see #hasDied()
     */
    protected SimpleDoubleProperty health = new SimpleDoubleProperty(1);

    /**
     * The maximum health of the monster.
     */
    protected double maxHealth = 1;

    /**
     * The current speed of the monster.
     */
    protected double speed = 1;

    /**
     * The number of pixels the monster can normally travel per frame.
     */
    protected double baseSpeed = 1;

    /**
     * The non-integral portion of the movement during each frame is accumulated here.
     * When it reaches one, it is consumed to allow the monster to move an extra pixel.
     */
    protected double unusedMovement = 0;

    /**
     * The amount of resources granted to the player on kill.
     */
    protected int resourceValue = 0;

    /**
     * A linked list of references to each status effect that is active against the monster.
     */
    @OneToMany
    protected LinkedList<StatusEffect> statusEffects = new LinkedList<>();

    /**
     * A linked list containing a reference to the positions that the monster has passed through in the previous frame.
     */
    @OneToMany
    protected LinkedList<ArenaObjectPositionInfo> trail = new LinkedList<>();

    protected void moveMonsterOneFrame() {
        trail.clear();
        unusedMovement += speed;

        short x = getX();
        short y = getY();
        while (unusedMovement >= 1) {
            ArenaScalarField<?>.ScalarFieldPoint nextPosition = gradientDescentField.descendTaxicab(x, y);
            if (nextPosition != null) {
                x = nextPosition.getX();
                y = nextPosition.getY();
                trail.add(new ArenaObjectPositionInfo(imageView, x, y));
            }

            unusedMovement--;
        }

        updatePosition(x, y);
    }

    protected void updateStatusEffects() {
        // Update status effects
        boolean isSlowed = false;
        for (StatusEffect se : statusEffects) {
            if (se.getEffectType() == StatusEffect.EffectType.Slow) {
                isSlowed = true;
            }
            se.countDown();
        }
        if (isSlowed) speed = baseSpeed * StatusEffect.SLOW_MULTIPLIER;
        else speed = baseSpeed;
        statusEffects.removeIf(x -> x.getDuration() <= 0);
    }

    // Define onNextFrame before constructor
    {
        onNextFrame = (sender, args) -> {
            moveMonsterOneFrame();
            updateStatusEffects();
        };
    }

    /**
     * Constructs a newly allocated {@link Monster} object and adds it to the {@link ArenaObjectStorage}.
     * @param storage The storage to add the object to.
     * @param imageView The ImageView to bound the object to.
     * @param x The x-coordinate of the object within the storage.
     * @param y The y-coordinate of the object within the storage.
     * @param difficulty The difficulty rating of the monster, which should be at least <code>1</code>.
     */
    public Monster(ArenaObjectStorage storage, ImageView imageView, short x, short y, double difficulty) {
        super(storage, imageView, x, y);

        if (difficulty < 1) throw new IllegalArgumentException("Difficulty should be at least equal to one.");
    }

    /**
     * Returns the current speed of the monster.
     * @return The current speed of the monster.
     */
    public final double getSpeed() { return speed; }

    /**
     * Returns the current health of the monster.
     * @return The current health of the monster.
     */
    public final double getHealth() { return health.get(); }

    /**
     * Accesses the amount of resources granted to the player by the monster on death.
     * @return The amount of resources granted to the player by the monster on death.
     */
    public final int getResourceValue() { return resourceValue; }

    /**
     * Reduces the health of the monster, and removes it from the arena if it dies.
     * 
     * @param amount The amount by which to reduce. If amount is not greater than <code>0</code> then nothing happens.
     */
    public final void takeDamage(double amount) {
        if (amount <= 0) return;
        this.health.set(getHealth() - amount);

        // Remove monster from arena if dead
        if (health.get() <= 0) {
            ArenaManager.getActiveEventRegister().ARENA_OBJECT_REMOVE.invoke(this,
                    new ArenaObjectEventArgs() {
                        { subject = Monster.this; }
                    }
            );
        }
    }

    /**
     * Accesses the status effects of the monster.
     * @return An iterator for the status effects of the monster.
     */
    public final Iterator<StatusEffect> getStatusEffects() { return statusEffects.iterator(); }
    /**
     * Adds a status effect to the monster.
     * @param statusEffect The status effect to add.
     */
    public final void addStatusEffect(StatusEffect statusEffect) { this.statusEffects.add(statusEffect); }

    @Override
    public final int compareTo(Monster o) {
        return Double.compare(getMovementDistanceToDestination(), o.getMovementDistanceToDestination());
    }

    @Override
    public LinkedList<ArenaObjectPositionInfo> getTrail() { return trail; }

    @Override
    public short getTargetLocationX() { return ArenaManager.END_X; }

    @Override
    public short getTargetLocationY() { return ArenaManager.END_Y; }

    @Override
    public double getMovementDistanceToDestination() {
        return ArenaManager.getActiveScalarFieldRegister().MONSTER_DISTANCE_TO_END.getValueAt(getX(), getY());
    }

    @Override
    public String getDisplayName() { return getClass().getSimpleName(); }
    
    @Override
    public String getDisplayDetails() { return String.format("HP: %.2f / %.2f", health, maxHealth); }
}