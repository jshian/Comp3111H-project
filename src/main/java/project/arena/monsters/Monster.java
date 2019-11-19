package project.arena.monsters;

import java.util.LinkedList;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.Tooltip;
import org.checkerframework.checker.nullness.qual.NonNull;

import javafx.scene.image.ImageView;
import project.arena.Arena;
import project.arena.Coordinates;
import project.arena.Grid;
import project.arena.ArenaMovingObject;

/**
 * Monsters spawn at the starting position and try to reach the end-zone of the arena.
 * They can only move horizontally or vertically towards an adjacent {@link Grid} that does not contain a Tower.
 * If they succeed, the game is lost.
 * Monsters do not have collision boxes, thus multiple of them can exist on the same pixel.
 */
@Entity
public abstract class Monster implements ArenaMovingObject, Comparable<Monster> {
    /**
     * ID for storage using Java Persistence API
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    /**
     * The ImageView that displays the monster.
     */
    @Transient
    protected ImageView imageView;

    /**
     * The Arena that this monster is attached to.
     */
    @NotNull
    @ManyToOne
    protected final Arena arena;

    /**
     * Represents the position of the monster.
     */
    @NotNull
    @OneToOne
    protected Coordinates coordinates;

    /**
     * The maximum health of the monster.
     */
    protected double maxHealth = 1;

    /**
     * The current health of the monster. It cannot go beyond {@link #maxHealth}.
     * When this is not greater than zero, the monster is considered dead.
     * @see #hasDied()
     */
    protected SimpleDoubleProperty health = new SimpleDoubleProperty(1);

    /**
     * The maximum number of pixels the monster can travel per frame.
     */
    protected double maxSpeed = 1;

    /**
     * The current speed of the monster. It cannot go beyond {@link #maxSpeed}.
     */
    protected double speed = 1;

    /**
     * The non-integral portion of the movement during each frame is accumulated here.
     * When it reaches one, it is consumed to allow the monster to move an extra pixel.
     */
    protected double unusedMovement = 0;

    /**
     * A linked list containing a reference to the coordinates that the monster has passed through in the previous frame.
     */
    protected LinkedList<Coordinates> prevCoordinates = new LinkedList<>();

    /**
     * The location which the monster tries to reach.
     */
    @OneToOne
    protected Coordinates destination;

    /**
     * The amount of resources granted to the player on kill.
     */
    protected int resources = 0;

    /**
     * A linked list of references to each status effect that is active against the monster.
     */
    @OneToMany
    protected LinkedList<StatusEffect> statusEffects = new LinkedList<>();

    /**
     * Constructor for the Monster class.
     * @param arena The arena the monster is attached to.
     * @param start The starting location of the monster.
     * @param destination The destination of the monster. It will try to move there.
     * @param imageView The ImageView that displays the monster.
     * @param difficulty The difficulty of the monster, which should be at least equal to <code>1</code>.
     */
    public Monster(@NonNull Arena arena, @NonNull Coordinates start, @NonNull Coordinates destination, ImageView imageView, double difficulty) {
        if (difficulty < 1) throw new IllegalArgumentException("Difficulty should be at least equal to one.");
        
        this.imageView = imageView;
        this.arena = arena;
        this.coordinates = new Coordinates(start);
        this.destination = new Coordinates(destination);

        this.coordinates.bindByImage(this.imageView);
        hoverMonsterEvent(this.arena);
    }

    /**
     * Constructor for making a copy of Monster class that is linked to another arena.
     * @param arena The arena to link this object to.
     * @param other The object to copy from.
     */
    public Monster(@NonNull Arena arena, @NonNull Monster other) {
        this.imageView = new ImageView(other.imageView.getImage());
        this.arena = arena;
        this.coordinates = new Coordinates(other.coordinates);
        this.maxHealth = other.maxHealth;
        this.health = new SimpleDoubleProperty(other.getHealth());
        this.maxSpeed = other.maxSpeed;
        this.speed = other.speed;
        this.unusedMovement = other.unusedMovement;
        for (Coordinates c : other.prevCoordinates) this.prevCoordinates.add(new Coordinates(c));
        this.destination = new Coordinates(other.destination);
        this.resources = other.resources;
        // hpLabel has already been deep copied
        for (StatusEffect se : other.statusEffects) this.statusEffects.add(new StatusEffect(se));

        this.coordinates.bindByImage(this.imageView);
        hoverMonsterEvent(arena);
    }

    /**
     * Creates a deep copy of the monster that is linked to another arena.
     * @param arena The arena to link this object to.
     * @return A deep copy of the monster.
     */
    public abstract Monster deepCopy(@NonNull Arena arena);
    
    // Interface implementation
    /**
     * {@inheritDoc}
     */
    @Override
    public final ImageView getImageView() { return imageView; }

    /**
     * {@inheritDoc}
     */
    @Override
    public final short getX() { return coordinates.getX(); }

    /**
     * {@inheritDoc}
     */
    @Override
    public final short getY() { return coordinates.getY(); }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setLocation(short x, short y) { this.coordinates.update(x, y); }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setLocation(@NonNull Coordinates coordinates) { this.coordinates.update(coordinates); }

    /**
     * {@inheritDoc}
     */
    @Override
    public final double getSpeed() { return speed; }

    /**
     * {@inheritDoc}
     */
    @Override
    public void nextFrame() {
        // Move monster
        prevCoordinates.clear();
        unusedMovement += speed;
        while (unusedMovement >= 1) {
            Coordinates nextCoordinates = findNextCoordinates();
            if (nextCoordinates != null) {
                prevCoordinates.add(nextCoordinates);
                coordinates.update(nextCoordinates);
            }

            unusedMovement--;
        }

        // Update status effects
        boolean isSlowed = false;
        for (StatusEffect se : statusEffects) {
            if (se.getEffectType() == StatusEffect.EffectType.Slow) {
                isSlowed = true;
            }
            se.countDown();
        }
        if (isSlowed) speed = maxSpeed * StatusEffect.SLOW_MULTIPLIER;
        else speed = maxSpeed;
        statusEffects.removeIf(x -> x.getDuration() <= 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int compareTo(Monster other) { return Integer.compare(this.distanceToDestination(), other.distanceToDestination()); }

    /**
     * Gets the class name of the monster.
     * @return The class name of the monster.
     */
    public abstract String getClassName();

    /**
     * Accesses the health of the monster.
     * @return The health of the monster.
     */
    public final double getHealth() { return health.get(); }

    /**
     * Sets the health of the monster.
     * @param value The new health of the monster.
     */
    protected final void setHealth(double value) { this.health.set(value); }

    /**
     * Accesses the coordinates that the monster has passed through in the previous frame.
     * @return A linked list containing a reference to the coordinates that the monster has passed through in the previous frame.
     */
    public final LinkedList<Coordinates> getPrevCoordinates() { return prevCoordinates; }

    /**
     * Accesses the amount of resources granted to the player by the monster on death.
     * @return The amount of resources granted to the player by the monster on death.
     */
    public final int getResources() { return resources; }

    /**
     * Accesses the status effects of the monster.
     * @return The status effects of the monster.
     */
    public final LinkedList<StatusEffect> getStatusEffects() { return statusEffects; }

    /**
     * Reduces the health of the monster.
     * @param amount The amount by which to reduce. If amount is not greater than <code>0</code> then nothing happens.
     */
    public final void takeDamage(double amount) {
        if (amount <= 0) return;
        this.health.set(getHealth() - amount);
    }

    /**
     * Adds a status effect to the monster.
     * @param statusEffect The status effect to add.
     */
    public final void addStatusEffect(StatusEffect statusEffect) { this.statusEffects.add(statusEffect); }

    /**
     * Determines whether the monster has died.
     * @return Whether the monster has died.
     */
    public final boolean hasDied() { return getHealth() <= 0; }

    /**
     * Finds the next coordinates to move to.
     * @return The next coordinates to move to.
     */
    public Coordinates findNextCoordinates() { return arena.findNextTowardsEnd_prioritizeMovement(coordinates); }

    /**
     * Finds the number of pixels the monster has to travel to reach its destination.
     * @return The number of pixels the monster has to travel to reach its destination.
     */
    public final short distanceToDestination() { return arena.getDistanceToEndZone(coordinates); }

    /**
     * show monster hp when mouse is hover over the monster.
     */
    protected void hoverMonsterEvent(Arena arena) {
        Tooltip tp = new Tooltip();
        tp.textProperty().bind(Bindings.format("hp: %.2f", health));

        imageView.setOnMouseEntered(e -> tp.show(imageView, e.getScreenX()+8, e.getScreenY()+7));
        imageView.setOnMouseMoved(e -> tp.show(imageView, e.getScreenX()+8, e.getScreenY()+7));
        imageView.setOnMouseExited(e -> tp.hide());
    }
}