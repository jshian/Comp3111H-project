package project.arena.projectiles;

import javax.persistence.Entity;

import org.checkerframework.checker.nullness.qual.NonNull;

import project.arena.Arena;
import project.arena.Coordinates;
import project.arena.monsters.Monster;

@Entity
public class BasicProjectile extends Projectile {

    /**
     * Constructor for the Projectile class.
     * @param arena The arena the projectile is attached to.
     * @param coordinates The coordinates of the pixel where the projectile is initially located.
     * @param target The monster that the projectile will pursue.
     * @param speed The speed of the projectile.
     * @param attackPower The attack power of the projectile.
     */
    public BasicProjectile(@NonNull Arena arena, @NonNull Coordinates coordinates, @NonNull Monster target, double speed, int attackPower) {
        super(arena,coordinates,target,speed,attackPower);
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
