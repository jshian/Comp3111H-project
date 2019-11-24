package project.entity;

import javafx.scene.image.ImageView;

import javax.persistence.Entity;

import project.query.ArenaObjectStorage;

/**
 * Projectile created by {@link BasicTower}.
 */
@Entity
public class BasicProjectile extends Projectile {

    /**
     * Constructs a newly allocated {@link BasicProjectile} object and adds it to the {@link ArenaObjectStorage}.
     * @param storage The storage to add the object to.
     * @param imageView The ImageView to bound the object to.
     * @param tower The tower from which this projectile originates.
     * @param target The monster that the projectile will pursue.
     * @param deltaX The x-offset from the targeted monster where the projectile will land.
     * @param deltaY The y-offset from the targeted monster where the projectile will land.
     */
    public BasicProjectile(ArenaObjectStorage storage, ImageView imageView, BasicTower tower, Monster target, short deltaX, short deltaY) {
        super(storage, imageView, tower, target, deltaX, deltaY);
    }
}
