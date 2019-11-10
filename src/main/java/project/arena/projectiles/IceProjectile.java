package project.arena.projectiles;

import java.util.EnumSet;
import java.util.LinkedList;

import org.checkerframework.checker.nullness.qual.NonNull;

import project.arena.Arena;
import project.arena.Coordinates;
import project.arena.ExistsInArena;
import project.arena.monsters.Monster;
import project.arena.monsters.StatusEffect;

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
     * @param target The Coordinates of Monster that the projectile will pursue, which should not be <code>null</code>.
     * @param speed The speed of the ice projectile.
     * @param slowDown The cold down time of the ice projectile.
     */
    public IceProjectile(@NonNull Arena arena, @NonNull Coordinates coordinates, @NonNull Coordinates target, double speed, int slowDown){
        super(arena,coordinates,target,speed,0);
        this.slowDownTime=slowDown;
    }

    /**
     * @see Projectile#Projectile(Projectile)
     */
    public IceProjectile(@NonNull IceProjectile other){
        super(other);
        this.slowDownTime = other.slowDownTime;
    }

    @Override
    public Projectile deepCopy() {
        return new IceProjectile(this);
    }

    /**
     * get slow down time of projectile.
     * @return slow down time of projectile.
     */
    public int getSlowDownTime() { return slowDownTime; }

    @Override
    public void nextFrame() {
        super.nextFrame();
        if (hasReachedTarget()){
            LinkedList<ExistsInArena> targets = arena.findObjectsInGrid(target, EnumSet.of(Arena.TypeFilter.Monster));
            if(targets.size()>0){
                Monster target = (Monster)targets.get(0);
                if (target != null) {
                    target.addStatusEffect(new StatusEffect(StatusEffect.EffectType.Slow, slowDownTime));
                    System.out.println(String.format("Ice Tower@(%d,%d) -> %s@(%d,%d)", tower.getX(), tower.getY()
                            , target.getClassName(), target.getX(), target.getY()));
                }
            }
        }
    }
}
