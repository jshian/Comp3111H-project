package project.arena.projectiles;

import java.util.EnumSet;
import java.util.LinkedList;

import org.checkerframework.checker.nullness.qual.NonNull;

import project.arena.Arena;
import project.arena.Coordinates;
import project.arena.ExistsInArena;
import project.arena.monsters.Monster;

public class BasicProjectile extends Projectile {

    /**
     * Constructor for the Projectile class.
     * @param arena The arena the projectile is attached to.
     * @param coordinates The coordinates of the pixel where the projectile is initially located.
     * @param target The Monster that the projectile will pursue, which should not be <code>null</code>.
     * @param speed The speed of the projectile.
     * @param attackPower The attack power of the projectile.
     */
    public BasicProjectile(@NonNull Arena arena, @NonNull Coordinates coordinates, @NonNull Coordinates target, double speed, int attackPower) {
        super(arena,coordinates,target,speed,attackPower);
    }

    /**
     * @see Projectile#Projectile(Projectile)
     */
    public BasicProjectile(@NonNull BasicProjectile other){
        super(other);
    }

    @Override
    public BasicProjectile deepCopy() {
        return new BasicProjectile(this);
    }

    @Override
    public void nextFrame() {
        super.nextFrame();
        if (hasReachedTarget()){
            LinkedList<ExistsInArena> targets = arena.findObjectsInGrid(target, EnumSet.of(Arena.TypeFilter.Monster));
            if(targets.size()>0){
                Monster target = (Monster)targets.get(0);
                if (target != null) {
                    target.takeDamage(attackPower);
                    System.out.println(String.format("Basic Tower@(%d,%d) -> %s@(%d,%d)", tower.getX(), tower.getY()
                            , target.getClassName(), target.getX(), target.getY()));
                }
            }

        }
    }
}
