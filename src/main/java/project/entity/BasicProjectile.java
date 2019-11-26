package project.entity;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.persistence.Entity;

import project.controller.ArenaManager;
import project.query.ArenaObjectStorage;

/**
 * Projectile created by {@link BasicTower}.
 */
@Entity
public class BasicProjectile extends Projectile {

    /**
     * Constructs a newly allocated {@link BasicProjectile} object and adds it to the {@link ArenaObjectStorage}.
     * @param storage The storage to add the object to.
     * @param tower The tower from which this projectile originates.
     * @param target The monster that the projectile will pursue.
     * @param deltaX The x-offset from the targeted monster where the projectile will land.
     * @param deltaY The y-offset from the targeted monster where the projectile will land.
     */
    public BasicProjectile(ArenaObjectStorage storage, BasicTower tower, Monster target, short deltaX, short deltaY) {
        super(storage, tower, target, deltaX, deltaY);
    }

    @Override
    protected ImageView getDefaultImage() {
        return new ImageView(new Image("/basicProjectile.png", ArenaManager.GRID_WIDTH / 8, ArenaManager.GRID_HEIGHT / 8, true, true));
    }
}
