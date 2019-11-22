package project.entity;

import javax.persistence.Entity;

import org.checkerframework.checker.nullness.qual.NonNull;

import javafx.scene.image.ImageView;
import project.arena.Arena;

@Entity
public class IceProjectile extends Projectile {
    /**
     * The slow down duration to monsters of projectile.
     */
    private int slowDownTime;

    /**
     * @see Projectile#Projectile(ArenaObjectStorage, ImageView, Tower, Monster, short, short)
     */
    public IceProjectile(ArenaObjectStorage storage, ImageView imageView, IceTower tower, Monster target, short deltaX, short deltaY) {
        super(storage, imageView, tower, target, deltaX, deltaY);
        this.slowDownTime = tower.getSlowDownTime();
    }

    /**
     * Returns the slow down time of the projectile.
     * @return The slow down time of the projectile.
     */
    public final int getSlowDownTime() { return slowDownTime; }

    @Override
    protected void damageTarget() {
        super.damageTarget();

        if (!target.hasDied()) {
            target.addStatusEffect(new StatusEffect(StatusEffect.EffectType.Slow, slowDownTime));
        }
    }
}
