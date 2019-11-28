package project.entity;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.persistence.Entity;

import project.control.ArenaManager;
import project.query.ArenaObjectPropertySelector;
import project.query.ArenaObjectStorage.StoredType;
import project.util.Geometry;

/**
 * Projectile created by {@link LaserTower}.
 */
@Entity
public class LaserProjectile extends Projectile {

    /**
     * The display duration of the laser beam.
     * Damage is still only applied on the first frame.
     */
    private static int LASER_DISPLAY_DURATION = 2;

    /**
     * Default constructor.
     */
    public LaserProjectile() {}

    /**
     * Constructs a newly allocated {@link LaserProjectile} object.
     * @param tower The tower from which this projectile originates.
     * @param target The monster that the projectile will pursue.
     * @param deltaX The x-offset from the targeted monster where the projectile will land.
     * @param deltaY The y-offset from the targeted monster where the projectile will land.
     */
    public LaserProjectile(LaserTower tower, Monster target, short deltaX, short deltaY) {
        super(tower, target, deltaX, deltaY);
    }

    @Override
    public void damageTarget() {
        // Don't call super method to prevent double hitting
        // super()

        ArenaManager.getActiveUIController().drawRay(origin.getX(), origin.getY(), getX(), getY(), LASER_DISPLAY_DURATION);

        ArenaObjectPropertySelector<Monster> selector = new ArenaObjectPropertySelector<>(Monster.class, o -> true);
        List<ArenaObject> monsters = storage.getQueryResult(selector, EnumSet.of(StoredType.MONSTER));
        for (ArenaObject m : monsters) {
            if (Geometry.isInRay(m.getX(),m.getY(), origin.getX(),origin.getY(),target.getX(),target.getY(), 3)) {
                ((Monster) m).takeDamage(damage, this);
            }
        }
    }

    @Override
    protected ImageView getDefaultImage() {
        return new ImageView(new Image("/laserProjectile.png", ArenaManager.GRID_WIDTH / 4, ArenaManager.GRID_HEIGHT / 4, true, true));
    }
}
