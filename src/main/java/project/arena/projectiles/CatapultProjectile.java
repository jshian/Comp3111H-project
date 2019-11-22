package project.arena.projectiles;

import javax.persistence.Entity;
import javax.persistence.PostLoad;

import javafx.scene.image.Image;
import org.checkerframework.checker.nullness.qual.NonNull;

import javafx.scene.image.ImageView;
import project.Geometry;
import project.UIController;
import project.arena.Arena;
import project.arena.monsters.Monster;
import project.arena.towers.Catapult;

@Entity
public class CatapultProjectile extends Projectile {

    /**
     * The damage range of projectile.
     */
    private short damageRange;

    /**
     * Projectile#Projectile(Arena, Tower, Monster, short, short)
     */
    public CatapultProjectile(@NonNull Arena arena, @NonNull Catapult tower, @NonNull Monster target, short deltaX, short deltaY) {
        super(arena, tower, target, deltaX, deltaY);
        this.damageRange = tower.getSplashRadius();
        loadImage();
    }

    /**
     * @see Projectile#Projectile(Arena, Monster, Projectile)
     */
    public CatapultProjectile(@NonNull Arena arena, @NonNull Monster target, @NonNull CatapultProjectile other){
        super(arena, target, other);
        this.damageRange = other.damageRange;
    }

    @Override
    public CatapultProjectile deepCopy(@NonNull Arena arena, @NonNull Monster target) {
        return new CatapultProjectile(arena, target, this);
    }

    @Override
    protected String getTowerClassName() { return "Catapult"; }

    @Override
    public void damageTarget() {
        super.damageTarget();

        arena.drawCircle(coordinates, damageRange);
        for (Monster m : arena.getMonsters()) {
            if (m == target) continue; // Don't double-hit

            if (Geometry.isInCircle(m.getX(), m.getY(), getX(), getY(), damageRange)) {
                if (!m.hasDied()) {
                    m.takeDamage(attackPower);
                    System.out.println(String.format("%s@(%d, %d) -> %s@(%d, %d)", getTowerClassName(), origin.getX(), origin.getY()
                    , m.getClassName(), m.getX(), m.getY()));
                }
            }
        }
    }

    /**
     * Load ImageView of monster.
     */
    @PostLoad
    public void loadImage() {
        imageView = new ImageView(new Image("/catapultProjectile.png", UIController.GRID_WIDTH / 2, UIController.GRID_HEIGHT / 2, true, true));
        coordinates.bindByImage(imageView);
    }
}
