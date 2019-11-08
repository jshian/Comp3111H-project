package project.monsters;

import project.*;
import project.Arena.MovesInArena;

import java.util.*;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.checkerframework.checker.nullness.qual.NonNull;

import javafx.scene.image.ImageView;

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
     * A linked list of references to the coordinates of the center of each grid which the monster will travel through in order to reach its {@link #destination}.
     */
    @Transient
    protected LinkedList<Coordinates> futurePath;

    /**
     * A linked list of references to each status effect that is active against the monster.
     */
    @ElementCollection
    protected List<StatusEffect> statusEffects;

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

        this.futurePath = new LinkedList<>();
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

        this.futurePath = new LinkedList<>();
        for (Coordinates c : other.futurePath) this.futurePath.add(new Coordinates(c));

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
    public void moveOneFrame() { if (!futurePath.isEmpty()) coordinates.update(futurePath.removeFirst()); }
    public int compareTo(Monster other) { return Integer.compare(this.distanceToDestination(), other.distanceToDestination()); }

    /**
     * Gets the class name of the monster.
     * @return The class name of the monster.
     */
    public abstract String getClassName();

    /**
     * Gets the coordinates of the monster in the next frame.
     * @return The coordinates of monster in the next frame.
     */
    public Coordinates getNextFrame() { return !futurePath.isEmpty() ? futurePath.removeFirst() : null; }

    /**
     * Accesses the health of the monster.
     * @return The health of the monster.
     */
    public double getHealth() { return health; }

    /**
     * Sets the health of the monster.
     * @param health The health of the monster.
     */
    public void setHealth(double health) {
        this.health = health < 0 ? 0 : health;
    }

    /**
     * Accesses the speed of the monster.
     * @return The speed of the monster.
     */
    public double getSpeed() { return speed; }

    /**
     * Sets the speed of the monster.
     * @param speed The speed of the monster.
     */
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    /**
     * Accesses the status effects of the monster.
     * @return The status effects of the monster.
     */
    public LinkedList<StatusEffect> getStatusEffects() { return statusEffects; }

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
    public int distanceToDestination() {
        if (futurePath == null) return 0;

        return futurePath.size();
    }

    /**
     * Recalculates the future path of the monster.
     */
    public void recalculateFuturePath() {
        futurePath = arena.findPathToEndZone(new Coordinates(getX(), getY()), false);
    }
}