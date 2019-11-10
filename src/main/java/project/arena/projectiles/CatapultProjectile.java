package project.arena.projectiles;

import java.util.EnumSet;
import java.util.LinkedList;

import org.checkerframework.checker.nullness.qual.NonNull;

import project.arena.Arena;
import project.arena.Coordinates;
import project.arena.ExistsInArena;
import project.arena.monsters.Monster;

public class CatapultProjectile extends Projectile {

    /**
     * The damage range of projectile.
     */
    private int damageRange;

    /**
     * Constructor for the Projectile class.
     * @param arena The arena the projectile is attached to.
     * @param coordinates The coordinates of the pixel where the catapult projectile is initially located.
     * @param target The Coordinates of Monster that the projectile will pursue, which should not be <code>null</code>.
     * @param speed The speed of the catapult projectile.
     * @param attackPower The attack power of the catapult projectile.
     */
    public CatapultProjectile(@NonNull Arena arena, @NonNull Coordinates coordinates, @NonNull Coordinates target, double speed, int attackPower, int damageRange){
        super(arena,coordinates,target,speed,attackPower);
        this.damageRange=damageRange;
    }

    /**
     * @see Projectile#Projectile(Projectile)
     */
    public CatapultProjectile(@NonNull CatapultProjectile other){
        super(other);
        this.damageRange = other.damageRange;
    }

    @Override
    public CatapultProjectile deepCopy() {
        return new CatapultProjectile(this);
    }

    @Override
    public void nextFrame() {
        super.nextFrame();
        if (hasReachedTarget()){
            //draw damage circle
            arena.drawCircle(target,damageRange);

            //give damage
            LinkedList<ExistsInArena> monsters = arena.findObjectsInRange(target, damageRange, EnumSet.of(Arena.TypeFilter.Monster));
            for (ExistsInArena monster : monsters) {
                if (monster instanceof Monster) {
                    ((Monster) monster).takeDamage(attackPower);
                    System.out.println(String.format("Catapult@(%d,%d) -> %s@(%d,%d)", tower.getX(), tower.getY()
                            , ((Monster) monster).getClassName(), monster.getX(), monster.getY()));
                }
            }
        }
    }
}
