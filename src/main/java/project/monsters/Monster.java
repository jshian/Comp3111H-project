package project.monsters;

import project.*;
import project.Arena.MovesInArena;

import java.util.*;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.apache.commons.lang3.*;
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
     * @param difficulty The difficulty of the monster.
     * @param start The starting location of the monster.
     * @param destination The destination of the monster. It will try to move there.
     */
    public Monster(double difficulty, @NonNull Coordinates start, @NonNull Coordinates destination) {
        this.coordinates = start;
        this.destination = destination;
        this.coordinates.bindByImage(this.imageView);
    }

    /**
     * Constructor for the Monster class.
     * @param difficulty The difficulty of the monster.
     * @param start The starting location of the monster.
     * @param destination The destination of the monster. It will try to move there.
     * @param imageView The imageView of the monster.
     */
    public Monster(double difficulty, @NonNull Coordinates start, @NonNull Coordinates destination, @NonNull ImageView imageView) {
        this.coordinates = start;
        this.destination = destination;
        this.imageView = imageView;
        this.coordinates.bindByImage(this.imageView);
    }
    
    // Inferface implementation
    public ImageView getImageView() { return imageView; }
    public int getX() { return coordinates.getX(); }
    public int getY() { return coordinates.getY(); }
    public void refreshDisplay() { throw new NotImplementedException("TODO"); }
    public void setLocation(int x, int y) { coordinates.update(x, y); }
    public Coordinates getNextFrame() { return !futurePath.isEmpty() ? futurePath.removeFirst() : null; }
    public void MoveOneFrame() { if (!futurePath.isEmpty()) coordinates.update(futurePath.removeFirst()); }
    public int compareTo(Monster other) { return Integer.compare(this.distanceToDestination(), other.distanceToDestination()); }

    /**
     * Sets the health of the monster.
     * @param health The health of the monster.
     */
    public void setHealth(double health) {
        this.health = health;
    }

    /**
     * Accesses the health of the monster.
     * @return The health of the monster.
     */
    public double getHealth() { return health; }

    /**
     * Sets the speed of the monster.
     * @param speed The speed of the monster.
     */
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    /**
     * Accesses the speed of the monster.
     * @return The speed of the monster.
     */
    public double getSpeed() { return speed; }

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
     * Recalculates the future path of the monster using A* search.
     */
    public void recalculateFuturePath() {
        class CoordinatesCostPair implements Comparable<CoordinatesCostPair> {
            private Coordinates coordinates;
            private double cost;    // Length of shortest path up to this point
            private double heruistic;   // Distance from coordinates to destination

            public CoordinatesCostPair(Coordinates coordinates, double cost, double heruistic) {
                this.coordinates = coordinates;
                this.cost = cost;
                this.heruistic = heruistic;
            }

            @Override
            public int compareTo(CoordinatesCostPair other) {
                return Double.compare(this.cost + this.heruistic, other.cost + other.heruistic);
            }
        }
        
        // Coordinates/Cost pair representing the center of each grid that has been searched
        PriorityQueue<CoordinatesCostPair> gridCenters = new PriorityQueue<>(
            (int) (Math.pow(2, (int) (Math.log(this.coordinates.taxicabDistanceFrom(this.destination)) / Math.log(2)) + 1))
        );
        gridCenters.add(new CoordinatesCostPair(this.coordinates, 0, this.coordinates.taxicabDistanceFrom(this.destination)));

        // Coordinates representing the center of the previous grid relative to this element on the least cost path
        Hashtable<Coordinates, Coordinates> previousGridFrom = new Hashtable<>();

        while (!gridCenters.isEmpty()) {
            CoordinatesCostPair currentPair = gridCenters.poll();

            // Check if reached destination
            if (currentPair.coordinates.isAt(this.destination)) {
                this.futurePath = new LinkedList<>();
                while (!previousGridFrom.isEmpty()) {
                    Coordinates prevCoordinates = previousGridFrom.remove(currentPair.coordinates);
                    futurePath.addFirst(prevCoordinates);
                }
                return;
            }

            // Add viable neighbours:
            // Monsters cannot move diagonally.
            LinkedList<Coordinates> neighbours = Arena.taxicabNeighbours(this.coordinates);
            for (Coordinates nextCoordinates : neighbours) {
                // Grid cannot contain a tower
                if (Arena.objectsInGrid(nextCoordinates, EnumSet.of(Arena.TypeFilter.Tower)).isEmpty()) {
                    double nextCost = Double.POSITIVE_INFINITY;
                    for (CoordinatesCostPair pair : gridCenters)
                        if (pair.coordinates.isAt(nextCoordinates))
                            nextCost = pair.cost;

                    if (currentPair.cost + 1 < nextCost) {
                        gridCenters.add(new CoordinatesCostPair(nextCoordinates,
                            currentPair.cost + 1,
                            nextCoordinates.taxicabDistanceFrom(this.destination)
                        ));
                        previousGridFrom.put(nextCoordinates, currentPair.coordinates);
                    }

                }
            }
        }
    }
}