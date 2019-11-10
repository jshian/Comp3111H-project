package project.arena.monsters;

import java.util.LinkedList;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.checkerframework.checker.nullness.qual.NonNull;

import javafx.scene.image.ImageView;
import project.arena.Arena;
import project.arena.Coordinates;
import project.arena.MovesInArena;

/**
 * Monsters spawn at the starting position and try to reach the end-zone of the arena.
 * They can only move horizontally or vertically towards an adjacent grid that does not contain a Tower.
 * If they succeed, the game is lost.
 * Monsters do not have collision boxes, thus multiple of them can exist on the same pixel.
 */
@Entity
public abstract class Monster implements MovesInArena, Comparable<Monster> {
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
    private ImageView imageView;

    /**
     * The Arena that this monster is attached to.
     */
    @Transient
    protected final Arena arena;

    /**
     * Represents the position of the monster.
     */
    @NotNull
    protected Coordinates coordinates;

    /**
     * The maximum health of the monster.
     */
    @NotNull
    protected double maxHealth;

    /**
     * The current health of the monster. It cannot go beyond {@link #maxHealth}.
     * When this is not greater than zero, the monster is considered dead.
     * @see #hasDied()
     */
    @NotNull
    protected double health;

    /**
     * The maximum number of pixels the monster can travel per frame.
     */
    @NotNull
    protected double maxSpeed;

    /**
     * The current speed of the monster. It cannot go beyond {@link #maxSpeed}.
     */
    @NotNull
    protected double speed;

    /**
     * The location which the monster tries to reach.
     */
    @NotNull
    protected Coordinates destination;

    /**
     * A linked list of references to each status effect that is active against the monster.
     */
    @ElementCollection
    protected LinkedList<StatusEffect> statusEffects;

    /**
     * Constructor for the Monster class.
     * @param arena The arena the monster is attached to.
     * @param start The starting location of the monster.
     * @param destination The destination of the monster. It will try to move there.
     * @param imageView The ImageView that displays the monster.
     * @param difficulty The difficulty of the monster.
     */
    public Monster(Arena arena, @NonNull Coordinates start, @NonNull Coordinates destination, ImageView imageView, double difficulty) {
        this.imageView = imageView;
        this.arena = arena;
        this.coordinates = new Coordinates(start);
        this.destination = new Coordinates(destination);
        this.coordinates.bindByImage(this.imageView);

        this.statusEffects = new LinkedList<>();
    }

    /**
     * Copy constructor for the Monster class. Performs deep copy.
     * @param other The other object to copy form.
     */
    public Monster(Monster other) {
        this.imageView = new ImageView(other.imageView.getImage());
        this.arena = other.arena;
        this.coordinates = new Coordinates(other.coordinates);
        this.maxHealth = other.maxHealth;
        this.health = other.health;
        this.maxSpeed = other.maxSpeed;
        this.speed = other.speed;
        this.destination = new Coordinates(other.destination);
        this.coordinates.bindByImage(this.imageView);

        this.statusEffects = new LinkedList<>();
        for (StatusEffect se : other.statusEffects) this.statusEffects.add(new StatusEffect(se));
    }

    /**
     * Creates a deep copy of the monster.
     * @return A deep copy of the monster.
     */
    public abstract Monster deepCopy();
    
    // Inferface implementation
    public ImageView getImageView() { return imageView; }
    public int getX() { return coordinates.getX(); }
    public int getY() { return coordinates.getY(); }
    public void setLocation(int x, int y) { this.coordinates.update(x, y); }
    public void setLocation(@NonNull Coordinates coordinates) { this.coordinates.update(coordinates); }
    public double getSpeed() { return speed; }
    public void nextFrame() {
        Coordinates nextCoordinates = arena.findNextTowardsEnd_prioritizeMovement(coordinates);
        if (nextCoordinates != null) coordinates.update(nextCoordinates);

        boolean isSlowed = false;
        for (StatusEffect se : statusEffects) {
            if (se.getEffectType() == StatusEffect.EffectType.Slow) {
                isSlowed = true;
            }
            se.countDown();
        }
        if (isSlowed) speed = maxSpeed / 5;
    }
    public int compareTo(Monster other) { return Integer.compare(this.distanceToDestination(), other.distanceToDestination()); }

    /**
     * Gets the class name of the monster.
     * @return The class name of the monster.
     */
    public abstract String getClassName();

    /**
     * Accesses the health of the monster.
     * @return The health of the monster.
     */
    public double getHealth() { return health; }

    /**
     * Accesses the status effects of the monster.
     * @return The status effects of the monster.
     */
    public LinkedList<StatusEffect> getStatusEffects() { return statusEffects; }

    /**
     * Reduces the health of the monster.
     * @param amount The amount by which to reduce.
     */
    public void takeDamage(double amount) {
        this.health -= amount;
    }

    /**
     * Adds a status effect to the monster.
     * @param statusEffect The status effect to add.
     */
    public void addStatusEffect(StatusEffect statusEffect) { this.statusEffects.add(statusEffect); }

    /**
     * Determines whether the monster has died.
     * @return Whether the monster has died.
     */
    public boolean hasDied() { return health <= 0; }

    /**
     * Finds the number of pixels the monster has to travel to reach its destination.
     * @return The number of pixels the monster has to travel to reach its destination.
     */
    public int distanceToDestination() { return arena.getDistanceToEndZone(coordinates); }
}