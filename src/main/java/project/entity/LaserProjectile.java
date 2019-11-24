package project.entity;

import java.util.PriorityQueue;

import javafx.scene.image.ImageView;

import javax.persistence.Entity;

import project.Geometry;
import project.arena.ArenaInstance;
import project.query.ArenaObjectStorage;

/**
 * Projectile created by {@link LaserTower}.
 */
@Entity
public class LaserProjectile extends Projectile {

    /**
     * Constructs a newly allocated {@link LaserProjectile} object and adds it to the {@link ArenaObjectStorage}.
     * @param storage The storage to add the object to.
     * @param imageView The ImageView to bound the object to.
     * @param tower The tower from which this projectile originates.
     * @param target The monster that the projectile will pursue.
     * @param deltaX The x-offset from the targeted monster where the projectile will land.
     * @param deltaY The y-offset from the targeted monster where the projectile will land.
     */
    public LaserProjectile(ArenaObjectStorage storage, ImageView imageView, LaserTower tower, Monster target, short deltaX, short deltaY) {
        super(storage, imageView, tower, target, deltaX, deltaY);
    }

    @Override
    public void nextFrame() {
        arena.drawRay(origin, new Coordinates(target.getX(), target.getY()));

        PriorityQueue<Monster> monsters = arena.getMonsters();
        for (Monster m : monsters) {
            if (Geometry.isInRay(m.getX(),m.getY(), origin.getX(),origin.getY(),target.getX(),target.getY(), 3)) {
                if (!target.hasDied()) {
                    m.takeDamage(this.damage);
                    System.out.println(String.format("Laser Tower@(%d,%d) -> %s@(%d,%d)", getX(), getY()
                            , m.getClassName(), m.getX(), m.getY()));
                }
            }
        }

        hasReachedTarget = true; // Hitscan
    }
}
