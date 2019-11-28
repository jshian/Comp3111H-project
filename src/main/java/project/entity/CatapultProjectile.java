package project.entity;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.EnumSet;
import java.util.List;

import javax.persistence.Entity;

import project.control.ArenaManager;
import project.query.ArenaObjectCircleSelector;
import project.query.ArenaObjectStorage.StoredType;

/**
 * Projectile created by {@link Catapult}.
 */
@Entity
public class CatapultProjectile extends Projectile {

    /**
     * The display duration of the splash effect.
     * Damage is still only applied on the first frame.
     */
    private static int SPLASH_DISPLAY_DURATION = 2;

    /**
     * The splash radius of the projectile.
     */
    private short splashRadius;

    /**
     * Default constructor.
     */
    public CatapultProjectile() {}

    /**
     * Constructs a newly allocated {@link CatapultProjectile} object.
     * @param tower The tower from which this projectile originates.
     * @param target The monster that the projectile will pursue.
     * @param deltaX The x-offset from the targeted monster where the projectile will land.
     * @param deltaY The y-offset from the targeted monster where the projectile will land.
     */
    public CatapultProjectile(Catapult tower, Monster target, short deltaX, short deltaY) {
        super(tower, target, deltaX, deltaY);
        this.splashRadius = tower.getSplashRadius();
    }

    /**
     * Returns the splash radius of the projectile.
     * @return The splash radius of the projectile.
     */
    public short getSplashRadius() {
        return splashRadius;
    }

    @Override
    public void damageTarget() {
        // Don't call super method to prevent double hitting
        // super()

        ArenaManager.getActiveUIController().drawCircle(getX(), getY(), splashRadius, SPLASH_DISPLAY_DURATION);

        ArenaObjectCircleSelector selector = new ArenaObjectCircleSelector(getX(), getY(), splashRadius);
        List<ArenaObject> monsters = storage.getQueryResult(selector, EnumSet.of(StoredType.MONSTER));
        for (ArenaObject m : monsters) {
            ((Monster) m).takeDamage(damage, this);
        }
    }

    @Override
    protected ImageView getDefaultImage() {
        return new ImageView(new Image("/catapultProjectile.png", ArenaManager.GRID_WIDTH / 2, ArenaManager.GRID_HEIGHT / 2, true, true));
    }
}
