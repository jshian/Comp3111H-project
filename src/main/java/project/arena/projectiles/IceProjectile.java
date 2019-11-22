package project.arena.projectiles;

import javax.persistence.Entity;
import javax.persistence.PostLoad;

import javafx.scene.image.Image;
import org.checkerframework.checker.nullness.qual.NonNull;

import javafx.scene.image.ImageView;
import project.UIController;
import project.arena.Arena;
import project.arena.monsters.Monster;
import project.arena.monsters.StatusEffect;
import project.arena.towers.IceTower;
import project.arena.towers.Tower;

@Entity
public class IceProjectile extends Projectile {
    /**
     * The slow down duration to monsters of projectile.
     */
    private int slowDownTime;

    /**
     * @see Projectile#Projectile(Arena, Tower, Monster, short, short)
     */
    public IceProjectile(@NonNull Arena arena, @NonNull IceTower tower, @NonNull Monster target, short deltaX, short deltaY) {
        super(arena, tower, target, deltaX, deltaY);
        this.slowDownTime = tower.getSlowDownTime();
        loadImage();
    }

    /**
     * @see Projectile#Projectile(Arena, Monster, Projectile)
     */
    public IceProjectile(@NonNull Arena arena, @NonNull Monster target, @NonNull IceProjectile other){
        super(arena, target, other);
        this.slowDownTime = other.slowDownTime;
    }

    @Override
    public IceProjectile deepCopy(@NonNull Arena arena, @NonNull Monster target) {
        return new IceProjectile(arena, target, this);
    }

    @Override
    protected String getTowerClassName() { return "Ice Tower"; }

    /**
     * get slow down time of projectile.
     * @return slow down time of projectile.
     */
    public final int getSlowDownTime() { return slowDownTime; }

    @Override
    protected void damageTarget() {
        super.damageTarget();

        if (!target.hasDied()) {
            target.addStatusEffect(new StatusEffect(StatusEffect.EffectType.Slow, slowDownTime));
        }
    }

    /**
     * Load ImageView of monster.
     */
    @PostLoad
    public void loadImage() {
        imageView = new ImageView(new Image("/iceProjectile.png", UIController.GRID_WIDTH / 8, UIController.GRID_HEIGHT / 8, true, true));
        coordinates.bindByImage(imageView);
    }
}
