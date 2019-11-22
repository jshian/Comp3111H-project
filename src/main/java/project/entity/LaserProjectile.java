package project.entity;

import java.util.PriorityQueue;

import javax.persistence.Entity;

import org.checkerframework.checker.nullness.qual.NonNull;

import javafx.scene.image.ImageView;
import project.Geometry;
import project.arena.Arena;
import project.arena.Coordinates;

@Entity
public class LaserProjectile extends Projectile {

    /**
     * @see Projectile#Projectile(ArenaObjectStorage, ImageView, Tower, Monster, short, short)
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
