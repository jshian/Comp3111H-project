package project.monsters;

import project.*;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.PriorityQueue;

import org.apache.commons.lang3.*;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Monsters spawn at the starting position and try to reach the end-zone of the arena.
 * They can only move horizontally or vertically towards an adjacent grid that does not contain a Tower.
 * If they succeed, the game is lost.
 * Monsters do not have collision boxes, thus multiple of them can exist on the same pixel.
 */
public abstract class Monster implements Arena.MovesInArena {
    // Position
    protected Coordinates coordinates;

    // Stats
    protected double maxHealth;
    protected double health;
    protected double maxSpeed;
    protected double speed;

    // Pathfinding
    protected Coordinates destination;
    protected LinkedList<Coordinates> futurePath;

    /**
     * Constructor for the Monster class.
     * @param difficulty The difficulty of the monster.
     * @param start The starting location of the monster.
     * @param destination The destination of the monster. It will try to move there.
     */
    public Monster(double difficulty, @NonNull Coordinates start, @NonNull Coordinates destination) {
        this.coordinates = start;
        this.destination = destination;
    }
    
    // Inferface implementation
    public void refreshDisplay() { throw new NotImplementedException("TODO"); }
    public Coordinates getCoordinates() { return coordinates; }
    public void MoveOneFrame() { if (!futurePath.isEmpty()) coordinates = futurePath.removeFirst(); }

    /**
     * Sets the health of the monster.
     * @param health The health of the monster.
     */
    public void setHealth(int health) {
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