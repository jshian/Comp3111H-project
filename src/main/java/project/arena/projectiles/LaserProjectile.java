package project.arena.projectiles;

import java.util.PriorityQueue;

import javax.persistence.Entity;

import org.checkerframework.checker.nullness.qual.NonNull;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import project.Geometry;
import project.UIController;
import project.arena.Arena;
import project.arena.Coordinates;
import project.arena.monsters.Monster;

@Entity
public class LaserProjectile extends Projectile {

    /**
     * Constructor for the Projectile class.
     * @param arena The arena the projectile is attached to.
     * @param coordinates The coordinates of the pixel where the projectile is initially located.
     * @param target The monster that the projectile will pursue.
     * @param attackPower The attack power of the projectile.
     */
    public LaserProjectile(Arena arena, @NonNull Coordinates coordinates, @NonNull Monster target, int attackPower) {
        super(arena,coordinates,target,Integer.MAX_VALUE,attackPower);//since laser attack is immediate so no need speed.
    }

    @Override
    protected void setupImage() {
        this.imageView = new ImageView(new Image("/laserProjectile.png", UIController.GRID_WIDTH / 4,
                UIController.GRID_HEIGHT / 4, true, true));

        this.coordinates.bindByImage(this.imageView);
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
