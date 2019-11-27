package project.entity;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.persistence.Entity;

import project.control.ArenaManager;

/**
 * Projectile created by {@link BasicTower}.
 */
@Entity
public class BasicProjectile extends Projectile {

    /**
     * Constructs a newly allocated {@link BasicProjectile} object.
     * @param tower The tower from which this projectile originates.
     * @param target The monster that the projectile will pursue.
     * @param deltaX The x-offset from the targeted monster where the projectile will land.
     * @param deltaY The y-offset from the targeted monster where the projectile will land.
     */
    public BasicProjectile(BasicTower tower, Monster target, short deltaX, short deltaY) {
        super(tower, target, deltaX, deltaY);
    }

    @Override
    protected ImageView getDefaultImage() {
        return new ImageView(new Image("/basicProjectile.png", ArenaManager.GRID_WIDTH / 8, ArenaManager.GRID_HEIGHT / 8, true, true));
    }
}
