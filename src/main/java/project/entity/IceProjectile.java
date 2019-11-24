package project.entity;

import javafx.scene.image.ImageView;

import javax.persistence.Entity;

import project.query.ArenaObjectStorage;

@Entity
public class IceProjectile extends Projectile {
    /**
     * The slow down duration to monsters of projectile.
     */
    private int slowDownTime;

    /**
     * Constructs a newly allocated {@link IceProjectile} object.
     * @param storage The storage to add the object to.
     * @param imageView The ImageView to bound the object to.
     * @param tower The tower from which this projectile originates.
     * @param target The monster that the projectile will pursue.
     * @param deltaX The x-offset from the targeted monster where the projectile will land.
     * @param deltaY The y-offset from the targeted monster where the projectile will land.
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
        target.addStatusEffect(new StatusEffect(StatusEffect.EffectType.Slow, slowDownTime));
    }
}
