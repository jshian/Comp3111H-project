package project;

import project.monsters.*;

import org.apache.commons.lang3.*;

/**
 * Projectiles are shot by a Tower towards Monsters and deal damage on contact. They disappear when they reach their target.
 * Projectiles do not have collision boxes, thus multiple of them can exist on the same pixel.
 */
public class Projectile implements Arena.MovesInArena {
    // Position
    private Coordinates coordinates;

    // Stats
    private double speed;

    // Target to pursue
    private Monster target;

    /**
     * Constructor for the Projectile class.
     * @param arena The arena in which the Projectile exists.
     */
    public Projectile(Arena arena) {
        this.coordinates = new Coordinates(arena);
    }

    // Inferface implementation
    public String getImagePath() { return new String(); }
    public Coordinates getCoordinates() { return coordinates; }
    public void MoveOneFrame() {
        Coordinates other = target.getCoordinates();
        double distance = coordinates.distanceFrom(other);

        if (distance <= speed)
            coordinates.update(other.getX(), other.getY());
        else {
            int newX = coordinates.getX() + (int) (speed * Math.cos(coordinates.angleFrom(other)));
            int newY = coordinates.getY() + (int) (speed * Math.sin(coordinates.angleFrom(other)));
            coordinates.update(newX, newY);
        }
    }
}