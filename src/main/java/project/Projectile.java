package project;

import project.monsters.*;

import org.apache.commons.lang3.*;

/**
 * Projectiles are shot by a Tower towards Monsters and deal damage on contact. They disappear when they reach their target.
 * Projectiles do not have collision boxes, thus multiple of them can exist on the same pixel.
 */
public abstract class Projectile implements Arena.MovesInArena {
    // Position
    private Coordinates coordinates;

    // Target to pursue
    private Monster target;

    // Stats
    private double speed;

    /**
     * Constructor for the Projectile class.
     * @param coordinates The coordinates of the pixel where the Projectile is initially located.
     * @param target The Monster that the Projectile will pursue.
     * @see Coordinates
     */
    public Projectile(Coordinates coordinates, Monster target, double speed) {
        this.coordinates = coordinates;
        this.target = target;
        this.speed = speed;
    }

    // Inferface implementation
    public void refreshDisplay() { throw new NotImplementedException("TODO"); }
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

    /**
     * Determines whether the Projectile has reached its target.
     * @return Whether the Projectile has reached its target.
     */
    public boolean hasReachedTarget() { return coordinates.isAt(target.getCoordinates()); }
}