package project.arena.projectiles;

import javax.persistence.Entity;

import org.checkerframework.checker.nullness.qual.NonNull;

import javafx.scene.image.ImageView;
import project.arena.Arena;
import project.arena.monsters.Monster;
import project.arena.towers.BasicTower;
import project.arena.towers.Tower;

@Entity
public class BasicProjectile extends Projectile {

    /**
     * @see Projectile#Projectile(Arena, Tower, Monster, short, short, ImageView)
     */
    public BasicProjectile(@NonNull Arena arena, @NonNull BasicTower tower, @NonNull Monster target, short deltaX, short deltaY, @NonNull ImageView imageView) {
        super(arena, tower, target, deltaX, deltaY, imageView);
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
}
