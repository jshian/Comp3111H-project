package project.arena.projectiles;

import java.util.PriorityQueue;

import javax.persistence.Entity;

import org.checkerframework.checker.nullness.qual.NonNull;

import javafx.scene.image.ImageView;
import project.Geometry;
import project.arena.Arena;
import project.arena.Coordinates;
import project.arena.monsters.Monster;
import project.arena.towers.LaserTower;

@Entity
public class LaserProjectile extends Projectile {

    /**
     * @see Projectile#Projectile(Arena, Tower, Monster, short, short, ImageView)
     */
    public LaserProjectile(@NonNull Arena arena, @NonNull LaserTower tower, @NonNull Monster target, short deltaX, short deltaY, @NonNull ImageView imageView) {
        super(arena, tower, target, deltaX, deltaY, imageView);
    }

    /**
     * @see Projectile#Projectile(Arena, Monster, Projectile)
     */
    public LaserProjectile(@NonNull Arena arena, @NonNull Monster target, @NonNull LaserProjectile other){
        super(arena, target, other);
    }

    @Override
    public LaserProjectile deepCopy(@NonNull Arena arena, @NonNull Monster target) {
        return new LaserProjectile(arena, target, this);
    }

    @Override
    protected String getTowerClassName() { return "Laser Tower"; }

    @Override
    public void nextFrame() {
        arena.drawRay(origin, new Coordinates(target.getX(), target.getY()));

        PriorityQueue<Monster> monsters = arena.getMonsters();
        for (Monster m : monsters) {
            if (Geometry.isInRay(m.getX(),m.getY(), origin.getX(),origin.getY(),target.getX(),target.getY(), 3)) {
                if (!target.hasDied()) {
                    m.takeDamage(this.attackPower);
                    System.out.println(String.format("Laser Tower@(%d,%d) -> %s@(%d,%d)", getX(), getY()
                            , m.getClassName(), m.getX(), m.getY()));
                }
            }
        }

        hasReachedTarget = true; // Hitscan
    }
}
