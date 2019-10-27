package project;

import java.util.LinkedList;

import org.apache.commons.lang3.NotImplementedException;

/**
 * Monsters spawn at the starting point and try to reach the end-point of the arena. If they succeed, the game is lost.
 * Monsters do not have collision boxes, thus multiple of them can exist on the same pixel.
 */
public abstract class Monster implements AppearsInArena {
    // Position
    private Coordinates coordinates;

    // Stats
    private int health, speed;

    // Pathfinding
    private LinkedList<Coordinates> futurePath = new LinkedList<>();
    
    // Inferface implementation
    public String getImagePath() { return new String(); }
    public Coordinates getCoordinates() { return coordinates; }

    /**
     * Moves the Monster by one time step.
     */
    public void move() {
        if (futurePath.isEmpty()) return;

        coordinates = futurePath.removeFirst();
    }

    /**
     * Recalculates the future path of the Monster. This is only needed if Tower placement changes.
     */
    public void recalculateFuturePath() {
        throw new NotImplementedException("TODO: A* Search");
    }
}