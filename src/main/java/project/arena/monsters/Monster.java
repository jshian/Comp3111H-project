package project.arena.monsters;

import java.util.LinkedList;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import org.checkerframework.checker.nullness.qual.NonNull;

import javafx.scene.image.ImageView;
import project.UIController;
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
    protected double maxHealth = 1;

    /**
     * The current health of the monster. It cannot go beyond {@link #maxHealth}.
     * When this is not greater than zero, the monster is considered dead.
     * @see #hasDied()
     */
    @NotNull
    protected SimpleDoubleProperty health = new SimpleDoubleProperty(1);

    /**
     * The maximum number of pixels the monster can travel per frame.
     */
    @NotNull
    protected double maxSpeed = 1;

    /**
     * The current speed of the monster. It cannot go beyond {@link #maxSpeed}.
     */
    @NotNull
    protected double speed = 1;

    /**
     * The location which the monster tries to reach.
     */
    @NotNull
    protected Coordinates destination;

    /**
     * The non-integral portion of the movement during each frame is accumulated here.
     * When it reaches one, it is consumed to allow the monster to move an extra pixel.
     */
    @NotNull
    protected double unusedMovement = 0;

    /**
     * The amount of resources granted to the player on kill.
     */
    @NotNull
    protected int resources = 0;

    /**
     * A linked list of references to each status effect that is active against the monster.
     */
    @ElementCollection
    protected LinkedList<StatusEffect> statusEffects = new LinkedList<>();

    /**
     * Constructor for the Monster class.
     * @param arena The arena the monster is attached to.
     * @param start The starting location of the monster.
     * @param destination The destination of the monster. It will try to move there.
     * @param imageView The ImageView that displays the monster.
     * @param difficulty The difficulty of the monster.
     */
    public Monster(@NonNull Arena arena, @NonNull Coordinates start, @NonNull Coordinates destination, ImageView imageView, double difficulty) {
        this.imageView = imageView;
        this.arena = arena;
        this.coordinates = new Coordinates(start);
        this.destination = new Coordinates(destination);
        this.coordinates.bindByImage(this.imageView);
        hoverMonsterEvent(this.arena);
    }

    /**
     * Copy constructor for the Monster class. Performs deep copy.
     * @param other The other object to copy form.
     */
    public Monster(@NonNull Monster other) {
        this.imageView = new ImageView(other.imageView.getImage());
        this.arena = other.arena;
        this.coordinates = new Coordinates(other.coordinates);
        this.maxHealth = other.maxHealth;
        this.health = new SimpleDoubleProperty(other.getHealth());
        this.maxSpeed = other.maxSpeed;
        this.speed = other.speed;
        this.destination = new Coordinates(other.destination);
        this.coordinates.bindByImage(this.imageView);
        hoverMonsterEvent(this.arena);

        for (StatusEffect se : other.statusEffects) this.statusEffects.add(new StatusEffect(se));
    }

    /**
     * Creates a deep copy of the monster.
     * @return A deep copy of the monster.
     */
    public abstract Monster deepCopy();
    
    // Interface implementation
    public ImageView getImageView() { return imageView; }
    public int getX() { return coordinates.getX(); }
    public int getY() { return coordinates.getY(); }
    public void setLocation(int x, int y) { this.coordinates.update(x, y); }
    public void setLocation(@NonNull Coordinates coordinates) { this.coordinates.update(coordinates); }
    public double getSpeed() { return speed; }
    public void nextFrame() {
        // Move monster
        unusedMovement += speed;
        while (unusedMovement >= 1) {
            Coordinates nextCoordinates = findNextCoordinates();
            if (nextCoordinates != null) coordinates.update(nextCoordinates);

            unusedMovement--;
        }

        // Update speed
        boolean isSlowed = false;
        for (StatusEffect se : statusEffects) {
            if (se.getEffectType() == StatusEffect.EffectType.Slow) {
                isSlowed = true;
            }
            se.countDown();
        }
        if (isSlowed) speed = maxSpeed * StatusEffect.SLOW_MULTIPLIER;
        else speed = maxSpeed;
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
    public double getHealth() { return health.get(); }

    /**
     * Accesses the amount of resources granted to the player by the monster on death.
     * @return The amount of resources granted to the player by the monster on death.
     */
    public int getResources() { return resources; }

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
        this.health.set(getHealth() - amount);
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
    public boolean hasDied() { return getHealth() <= 0; }

    /**
     * Finds the next coordinates to move to.
     * @return The next coordinates to move to.
     */
    public Coordinates findNextCoordinates() { return arena.findNextTowardsEnd_prioritizeMovement(coordinates); }

    /**
     * Finds the number of pixels the monster has to travel to reach its destination.
     * @return The number of pixels the monster has to travel to reach its destination.
     */
    public int distanceToDestination() { return arena.getDistanceToEndZone(coordinates); }

    /**
     * show monster hp when mouse is hover over the monster.
     */
    private void hoverMonsterEvent(Arena arena) {
        Label hpLabel = new Label();
        hpLabel.textProperty().bind(Bindings.format("hp: %.2f", health));
        hpLabel.setAlignment(Pos.CENTER);

        this.imageView.setOnMouseEntered(e -> {
            hpLabel.setLayoutX(imageView.getX());
            hpLabel.setLayoutY(imageView.getY());
            arena.getPane().getChildren().add(hpLabel);
        });

        this.imageView.setOnMouseExited(e -> {
            arena.getPane().getChildren().remove(hpLabel);
        });
    }
}