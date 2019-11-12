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
import javafx.geometry.Pos;
import javafx.scene.control.Label;
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
 * TODO: Allow Towers to shoot monsters that are fast.
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
     * The location which the monster tries to reach.
     */
    @OneToOne
    protected Coordinates destination;

    /**
     * The non-integral portion of the movement during each frame is accumulated here.
     * When it reaches one, it is consumed to allow the monster to move an extra pixel.
     */
    protected double unusedMovement = 0;

    /**
     * The amount of resources granted to the player on kill.
     */
    protected int resources = 0;

    /**
     * Label that display the hp of the monster.
     */
    @Transient
    protected Label hpLabel = new Label();

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
        this.destination = new Coordinates(other.destination);
        this.coordinates.bindByImage(this.imageView);
        hoverMonsterEvent(arena);

        for (StatusEffect se : other.statusEffects) this.statusEffects.add(new StatusEffect(se));
    }

    /**
     * Creates a deep copy of the monster that is linked to another arena.
     * @param arena The arena to link this object to.
     * @return A deep copy of the monster.
     */
    public abstract Monster deepCopy(@NonNull Arena arena);
    
    // Interface implementation
    public ImageView getImageView() { return imageView; }
    public short getX() { return coordinates.getX(); }
    public short getY() { return coordinates.getY(); }
    public void setLocation(short x, short y) { this.coordinates.update(x, y); }
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
     * Sets the health of the monster.
     * @param value The new health of the monster.
     */
    protected void setHealth(double value) { this.health.set(value); }

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
    public short distanceToDestination() { return arena.getDistanceToEndZone(coordinates); }

    /**
     * show monster hp when mouse is hover over the monster.
     */
    private void hoverMonsterEvent(Arena arena) {
        hpLabel.textProperty().bind(Bindings.format("hp: %.2f", health));
        hpLabel.setAlignment(Pos.CENTER);
        //seems like layout can only bindBidirectional, error otherwise.
        hpLabel.layoutXProperty().bindBidirectional(imageView.xProperty());
        hpLabel.layoutYProperty().bindBidirectional(imageView.yProperty());

        this.imageView.setOnMouseEntered(e -> {
            arena.getPane().getChildren().add(hpLabel);
        });

        this.imageView.setOnMouseExited(e -> {
            if (arena.getPane().getChildren().contains(hpLabel))
                arena.getPane().getChildren().remove(hpLabel);
        });
    }

    /**
     * get the Label displaying hp of monster.
     * @return the Label displaying hp of monster.
     */
    public Label getHpLabel() { return hpLabel; }
}