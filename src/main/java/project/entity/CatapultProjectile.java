package project.entity;

import javafx.scene.image.ImageView;

import javax.persistence.Entity;

import project.Geometry;
import project.arena.ArenaInstance;
import project.query.ArenaObjectStorage;

@Entity
public class CatapultProjectile extends Projectile {

    /**
     * The splash radius of the projectile.
     */
    private short splashRadius;

    /**
     * Constructs a newly allocated {@link CatapultProjectile} object.
     * @param storage The storage to add the object to.
     * @param imageView The ImageView to bound the object to.
     * @param tower The tower from which this projectile originates.
     * @param target The monster that the projectile will pursue.
     * @param deltaX The x-offset from the targeted monster where the projectile will land.
     * @param deltaY The y-offset from the targeted monster where the projectile will land.
     */
    public CatapultProjectile(ArenaObjectStorage storage, ImageView imageView, Catapult tower, Monster target, short deltaX, short deltaY) {
        super(storage, imageView, tower, target, deltaX, deltaY);
        this.splashRadius = tower.getSplashRadius();
    }

    /**
     * Returns the splash radius of the projectile.
     * @return The splash radius of the projectile.
     */
    public short getSplashRadius() {
        return splashRadius;
    }

    @Override
    public void damageTarget() {
        super.damageTarget();

        arena.drawCircle(coordinates, splashRadius);
        for (Monster m : arena.getMonsters()) {
            if (m == target) continue; // Don't double-hit

            if (Geometry.isInCircle(m.getX(), m.getY(), getX(), getY(), splashRadius)) {
                if (!m.hasDied()) {
                    m.takeDamage(damage);
                    System.out.println(String.format("%s@(%d, %d) -> %s@(%d, %d)", getTowerClassName(), origin.getX(), origin.getY()
                    , m.getClassName(), m.getX(), m.getY()));
                }
            }
        }
    }
}
