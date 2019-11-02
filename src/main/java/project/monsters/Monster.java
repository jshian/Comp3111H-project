package project.monsters;

import project.*;

import java.util.LinkedList;

import org.apache.commons.lang3.*;

/**
 * Monsters spawn at the starting position and try to reach the end-zone of the arena. If they succeed, the game is lost.
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
    protected LinkedList<Coordinates> futurePath = new LinkedList<>();

    /**
     * Constructor for the Monster class.
     * @param arena The arena in which the Monster exists.
     * @param difficulty The difficulty of the Monster.
     */
    public Monster(Arena arena, double difficulty) {
        this.coordinates = new Coordinates(arena);
    }
    
    // Inferface implementation
    public void refreshDisplay() { throw new NotImplementedException("TODO"); }
    public Coordinates getCoordinates() { return coordinates; }
    public void MoveOneFrame() { if (!futurePath.isEmpty()) coordinates = futurePath.removeFirst(); }

    /**
     * Accesses the health of the monster.
     * @return The health of the monster.
     */
    public double getHealth() { return health; }

    /**
     * Accesses the speed of the monster.
     * @return The speed of the monster.
     */
    public double getSpeed() { return speed; }

    /**
     * Causes the Monster to take damage.
     * @param amount The amount of damage to be taken.
     */
    public void takeDamage(double amount) { health -= amount; }

    /**
     * Determines whether the Monster has died.
     * @return Whether the Monster has died.
     */
    public boolean hasDied() { return health <= 0; }

    /**
     * Recalculates the future path of the Monster.
     */
    public void recalculateFuturePath() {
        throw new NotImplementedException("TODO: A* Search");
    }
}