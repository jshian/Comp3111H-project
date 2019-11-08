package project.projectiles;

import javafx.scene.shape.Line;
import org.checkerframework.checker.nullness.qual.NonNull;
import project.Arena;
import project.Coordinates;
import project.Geometry;
import project.monsters.Monster;

import java.util.PriorityQueue;

public class LaserProjectile extends Projectile{

    /**
     * Constructor for the Projectile class.
     * @param arena The arena the projectile is attached to.
     * @param coordinates The coordinates of the pixel where the projectile is initially located.
     * @param target The Monster that the projectile will pursue, which should not be <code>null</code>.
     * @param attackPower The attack power of the projectile.
     */
    public LaserProjectile(Arena arena, @NonNull Coordinates coordinates, @NonNull Coordinates target, int attackPower) {
        super(arena,coordinates,target,0,attackPower);//since laser attack is immediate so no need speed.
    }

    /**
     * @see Projectile#Projectile(Projectile)
     */
    public LaserProjectile(LaserProjectile other){
        super(other);
    }

    @Override
    public LaserProjectile deepCopy() {
        return new LaserProjectile(this);
    }

    @Override
    public void nextFrame() {
        arena.drawRay(tower, target);

        PriorityQueue<Monster> monsters = arena.getMonsters();
        for (Monster m : monsters) {
            if (Geometry.isInRay(m.getX(),m.getY(), tower.getX(),tower.getY(),target.getX(),target.getY(), 3)) {
                m.takeDamage(m.getHealth() - this.attackPower);
                System.out.println(String.format("Laser Tower@(%d,%d) -> %s@(%d,%d)", getX(), getY()
                        , m.getClassName(), m.getX(), m.getY()));
            }
        }

        coordinates.update(target.getX(),target.getY());//make sure hasReachedTarget return true
    }
}
