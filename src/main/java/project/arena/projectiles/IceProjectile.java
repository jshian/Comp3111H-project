package project.arena.projectiles;

import java.util.EnumSet;
import java.util.LinkedList;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

import org.checkerframework.checker.nullness.qual.NonNull;

import project.arena.Arena;
import project.arena.Coordinates;
import project.arena.ExistsInArena;
import project.arena.monsters.Monster;
import project.arena.monsters.StatusEffect;

@Entity
public class IceProjectile extends Projectile {
    /**
     * The slow down duration to monsters of projectile.
     */
    private int slowDownTime;

    /**
     * The slow down speed to monsters of projectile.
     */
    private final int slowDownSpeed = 5;

    /**
     * Constructor for the Projectile class.
     * @param arena The arena the projectile is attached to.
     * @param coordinates The coordinates of the pixel where the ice projectile is initially located.
     * @param target The monster that the projectile will pursue.
     * @param speed The speed of the ice projectile.
     * @param slowDown The cold down time of the ice projectile.
     */
    public IceProjectile(@NonNull Arena arena, @NonNull Coordinates coordinates, @NonNull Monster target, double speed, int slowDown){
        super(arena,coordinates,target,speed,0);
        this.slowDownTime=slowDown;
    }

    /**
     * @see Projectile#Projectile(Arena, Monster, Projectile)
     */
    public IceProjectile(@NonNull Arena arena, @NonNull Monster target, @NonNull IceProjectile other){
        super(arena, target, other);
        this.slowDownTime = other.slowDownTime;
    }

    @Override
    public IceProjectile deepCopy(@NonNull Arena arena, @NonNull Monster target) {
        return new IceProjectile(arena, target, this);
    }

    @Override
    protected String getTowerClassName() { return "Ice Tower"; }

    /**
     * get slow down time of projectile.
     * @return slow down time of projectile.
     */
    public int getSlowDownTime() { return slowDownTime; }

    @Override
    protected void damageTarget() {
        super.damageTarget();
        target.addStatusEffect(new StatusEffect(StatusEffect.EffectType.Slow, slowDownTime));
    }
}
