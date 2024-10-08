package project.entity;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.*;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.Tooltip;
import project.control.ArenaManager;
import project.field.ArenaScalarField;

/**
 * Monsters spawn at the starting position and try to reach the end-zone of the arena.
 * They can only move horizontally or vertically towards an adjacent Grid that does not contain a Tower.
 * If they succeed, the game is lost.
 * Monsters do not have collision boxes, thus multiple of them can exist on the same pixel.
 */
@Entity
public abstract class Monster extends ArenaObject implements Comparable<Monster>, InformativeObject, ObjectWithTarget, ObjectWithTrail {

    /**
     * The display duration of the death animation.
     */
    public static int DEATH_DISPLAY_DURATION = 5;

    /**
     * ID for storage using Java Persistence API
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The scalar field used to determine the movement of the monster via gradient descent.
     */
    @Transient
    protected ArenaScalarField<?> gradientDescentField = ArenaManager.getActiveScalarFieldRegister().MONSTER_DISTANCE_TO_END;

    /**
     * The current health of the monster. It cannot go beyond {@link #maxHealth}.
     * When this is not greater than zero, the monster is considered dead.
     */
    @Transient
    protected SimpleDoubleProperty healthProperty = new SimpleDoubleProperty(1);

    // use a redundant variable to store hp in jpa so that don't need to add getter/setter for all fields.
    /**
     * The current health of monster with type double. It is only used for saving.
     */
    protected double health = healthProperty.get();

    /**
     * The maximum health of the monster.
     */
    @Transient
    protected SimpleDoubleProperty maxHealthProperty = new SimpleDoubleProperty(1);

    // use a redundant variable to store hp in jpa so that don't need to add getter/setter for all fields.
    /**
     * The maximum health of the monster.
     */
    protected double maxHealth = maxHealthProperty.get();

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
    @OneToMany(cascade = {CascadeType.MERGE}, fetch=FetchType.EAGER)
    protected List<StatusEffect> statusEffects = new LinkedList<>();

    /**
     * A linked list containing a reference to the positions that the monster has passed through in the previous frame.
     */
    @OneToMany(cascade = {CascadeType.MERGE})
    protected List<ArenaObjectPositionInfo> trail = new LinkedList<>();

    protected void moveMonsterOneFrame() {
        trail = new LinkedList<>();
        unusedMovement += speed;

        short x = getX();
        short y = getY();
        while (unusedMovement >= 1) {
            ArenaScalarField.ScalarFieldPoint nextPosition = gradientDescentField.descendTaxicab(x, y);
            if (nextPosition != null) {
                x = nextPosition.getX();
                y = nextPosition.getY();
                trail.add(new ArenaObjectPositionInfo(imageView, x, y));
            }

            unusedMovement--;
        }

        moveObject(this, x, y);
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
     * Default constructor.
     */
    public Monster() {}

    /**
     * {@inheritDoc}
     * Initialise healthProperty from health when initialising from jpa.
     */
    @Override
    @PostLoad
    public void loadArenaObject() {
        super.loadArenaObject();
        this.healthProperty.set(health);
        this.maxHealthProperty.set(maxHealth);
        setupTooltip();
    }

    /**
     * {@inheritDoc}
     * Initialise the gradientDescentField of the monster.
     */
    public void initialiseGradientDescentField() {
        this.gradientDescentField = ArenaManager.getActiveScalarFieldRegister().MONSTER_DISTANCE_TO_END;
    }

    /**
     * Setup tooltip to display hp of monster.
     */
    public void setupTooltip() {
        // Set up tooltip
        Tooltip tp = new Tooltip();

        //getDisplayDetails() is fixed so even you bind it to a property, it wont change.
        tp.textProperty().bind(Bindings.format("HP: %.2f / %.2f", healthProperty, maxHealthProperty));
        imageView.setOnMouseEntered(e -> tp.show(imageView, e.getScreenX()+8, e.getScreenY()+7));
        imageView.setOnMouseMoved(e -> tp.show(imageView, e.getScreenX()+8, e.getScreenY()+7));
        imageView.setOnMouseExited(e -> tp.hide());
    }

    /**
     * Constructs a newly allocated {@link Monster} object.
     * @param x The x-coordinate of the object within the storage.
     * @param y The y-coordinate of the object within the storage.
     * @param difficulty The difficulty rating of the monster, which should be at least <code>1</code>.
     */
    public Monster(short x, short y, double difficulty) {
        super(x, y);

        if (difficulty < 1) throw new IllegalArgumentException("Difficulty should be at least equal to one.");

        setupTooltip();
    }

    /**
     * Returns the current speed of the monster.
     * @return The current speed of the monster.
     */
    public double getSpeed() { return speed; }

    /**
     * Returns the current health of the monster.
     * @return The current health of the monster.
     */
    public double getHealth() { return health; }

    /**
     * Returns the maximum health of the monster.
     * @return The maximum health of the monster.
     */
    protected double getMaxHealth() { return maxHealth; }

    /**
     * Accesses the amount of resources granted to the player by the monster on death.
     * @return The amount of resources granted to the player by the monster on death.
     */
    public int getResourceValue() { return resourceValue; }


    /**
     * Sets the current health of the monster.
     * @param value The new value.
     */
    protected void setHealth(double value) {
        healthProperty.set(value);
        health = value;
    }

    /**
     * Sets the maximum health of the monster.
     * @param value The new value.
     */
    protected void setMaxHealth(double value) {
         maxHealthProperty.set(value);
         maxHealth = value;
    }

    /**
     * Reduces the health of the monster, and removes it from the arena if it dies.
     * @param amount The amount by which to reduce. If amount is not greater than <code>0</code> then nothing happens.
     * @param attacker The object which damaged the monster.
     */
    public void takeDamage(double amount, Object attacker) {
        if (healthProperty.get() <= 0) return; // Already dead
        if (amount <= 0) return; // No damage dealt

        this.healthProperty.set(getHealth() - amount);
        this.health = healthProperty.get();

        // Remove monster from arena if dead
        if (healthProperty.get() <= 0) ArenaObjectFactory.removeObject(attacker, this);
    }

    /**
     * Accesses the status effects of the monster.
     * @return An iterator for the status effects of the monster.
     */
    public Iterator<StatusEffect> getStatusEffects() { return statusEffects.iterator(); }
    /**
     * Adds a status effect to the monster.
     * @param statusEffect The status effect to add.
     */
    public final void addStatusEffect(StatusEffect statusEffect) { this.statusEffects.add(statusEffect); }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(Monster o) {
        return Double.compare(getMovementDistanceToDestination(), o.getMovementDistanceToDestination());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ArenaObjectPositionInfo> getTrail() { return trail; }

    /**
     * {@inheritDoc}
     */
    @Override
    public short getTargetLocationX() { return ArenaManager.END_X; }

    /**
     * {@inheritDoc}
     */
    @Override
    public short getTargetLocationY() { return ArenaManager.END_Y; }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getMovementDistanceToDestination() {
        return ArenaManager.getActiveScalarFieldRegister().MONSTER_DISTANCE_TO_END.getValueAt(getX(), getY());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayName() { return getClass().getSimpleName(); }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayDetails() { return String.format("HP: %.2f / %.2f", getHealth(), maxHealth); }
}