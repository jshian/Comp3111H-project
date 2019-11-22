package project.entity;

import javax.persistence.Entity;

import org.checkerframework.checker.nullness.qual.NonNull;

import javafx.scene.image.ImageView;
import project.Geometry;
import project.arena.Arena;

@Entity
public class CatapultProjectile extends Projectile {

    /**
     * The splash radius of the projectile.
     */
    private short splashRadius;

    /**
     * @see Projectile#Projectile(ArenaObjectStorage, ImageView, Tower, Monster, short, short)
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
