package project.arena.projectiles;

import javax.persistence.Entity;
import javax.persistence.PostLoad;

import javafx.scene.image.Image;
import org.checkerframework.checker.nullness.qual.NonNull;

import javafx.scene.image.ImageView;
import project.UIController;
import project.arena.Arena;
import project.arena.monsters.Monster;
import project.arena.towers.BasicTower;
import project.arena.towers.Tower;

@Entity
public class BasicProjectile extends Projectile {

    /**
     * @see Projectile#Projectile(Arena, Tower, Monster, short, short)
     */
    public BasicProjectile(@NonNull Arena arena, @NonNull BasicTower tower, @NonNull Monster target, short deltaX, short deltaY) {
        super(arena, tower, target, deltaX, deltaY);
        loadImage();
    }

    /**
     * @see Projectile#Projectile(Arena, Monster, Projectile)
     */
    public BasicProjectile(@NonNull Arena arena, @NonNull Monster target, @NonNull BasicProjectile other){
        super(arena, target, other);
    }

    @Override
    public BasicProjectile deepCopy(@NonNull Arena arena, @NonNull Monster target) {
        return new BasicProjectile(arena, target, this);
    }

    @Override
    protected String getTowerClassName() { return "Basic Tower"; }

    /**
     * Load ImageView of monster.
     */
    @PostLoad
    public void loadImage() {
        imageView = new ImageView(new Image("/basicProjectile.png", UIController.GRID_WIDTH / 8, UIController.GRID_HEIGHT / 8, true, true));
        coordinates.bindByImage(imageView);
    }
}
