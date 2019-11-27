package project.entity;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.persistence.Entity;

import project.control.ArenaManager;

/**
 * Projectile created by {@link IceTower}.
 */
@Entity
public class IceProjectile extends Projectile {
    /**
     * The slow down duration to monsters of projectile.
     */
    private int slowDownTime;

    /**
     * Constructs a newly allocated {@link IceProjectile} object.
     * @param tower The tower from which this projectile originates.
     * @param target The monster that the projectile will pursue.
     * @param deltaX The x-offset from the targeted monster where the projectile will land.
     * @param deltaY The y-offset from the targeted monster where the projectile will land.
     */
    public IceProjectile(IceTower tower, Monster target, short deltaX, short deltaY) {
        super(tower, target, deltaX, deltaY);
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

    @Override
    protected ImageView getDefaultImage() {
        return new ImageView(new Image("/iceProjectile.png", ArenaManager.GRID_WIDTH / 8, ArenaManager.GRID_HEIGHT / 8, true, true));
    }
}
