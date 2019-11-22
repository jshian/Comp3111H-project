package project.entity;

import javax.persistence.Entity;

import org.checkerframework.checker.nullness.qual.NonNull;

import javafx.scene.image.ImageView;
import project.arena.Arena;

@Entity
public class BasicProjectile extends Projectile {

    /**
     * @see Projectile#Projectile(ArenaObjectStorage, ImageView, Tower, Monster, short, short)
     */
    public BasicProjectile(ArenaObjectStorage storage, ImageView imageView, BasicTower tower, Monster target, short deltaX, short deltaY) {
        super(storage, imageView, tower, target, deltaX, deltaY);
    }
}
