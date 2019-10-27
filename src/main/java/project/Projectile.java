package project;

import org.apache.commons.lang3.NotImplementedException;

/**
 * Projectiles are shot by a Tower towards Monsters and deal damage on contact. They disappear afterwards.
 * Projectiles do not have collision boxes, thus multiple of them can exist on the same pixel.
 */
public class Projectile implements AppearsInArena {
    // Position
    private Coordinates coordinates;

    // Stats
    private int speed;

    // Target to pursue
    private Monster target;

    // Inferface implementation
    public String getImagePath() { return new String(); }
    public Coordinates getCoordinates() { return coordinates; }

    /**
     * Moves the Projectile by one time step.
     */
    public void move() {
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