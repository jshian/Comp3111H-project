package project;

import java.util.LinkedList;

import org.apache.commons.lang3.*;

/**
 * Monsters spawn at the starting point and try to reach the end-point of the arena. If they succeed, the game is lost.
 * Monsters do not have collision boxes, thus multiple of them can exist on the same pixel.
 */
public abstract class Monster implements MovesInArena {
    // Position
    private Coordinates coordinates;

    // Stats
    private int health, speed;

    // Pathfinding
    private LinkedList<Coordinates> futurePath = new LinkedList<>();

    /**
     * Constructor for the Monster class.
     * @param arena The arena the Monster is in.
     */
    public Monster(Arena arena) {
        this.coordinates = new Coordinates(arena);
    }
    
    // Inferface implementation
    public String getImagePath() { return new String(); }
    public Coordinates getCoordinates() { return coordinates; }
    public void MoveOneFrame() { if (!futurePath.isEmpty()) coordinates = futurePath.removeFirst(); }

    /**
     * Accesses the health of the monster.
     * @return The health of the monster.
     */
    public int getHealth() { return health; }

    /**
     * Accesses the speed of the monster.
     * @return The speed of the monster.
     */
    public int getSpeed() { return speed; }
    

    /**
     * Causes the Monster to take damage.
     * @param amount The amount of damage to be taken.
     */
    public void takeDamage(int amount) { health -= amount; }

    /**
     * Recalculates the future path of the Monster. This is only needed if Tower placement changes.
     */
    public void recalculateFuturePath() {
        throw new NotImplementedException("TODO: A* Search");
    }
}