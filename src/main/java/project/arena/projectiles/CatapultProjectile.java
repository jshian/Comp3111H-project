package project.arena.projectiles;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

import org.checkerframework.checker.nullness.qual.NonNull;

import project.Geometry;
import project.arena.Arena;
import project.arena.Coordinates;
import project.arena.monsters.Monster;

@Entity
public class CatapultProjectile extends Projectile {

    /**
     * The damage range of projectile.
     */
    private short damageRange;

    /**
     * Constructor for the Projectile class.
     * @param arena The arena the projectile is attached to.
     * @param coordinates The coordinates of the pixel where the catapult projectile is initially located.
     * @param target The monster that the projectile will pursue.
     * @param deltaX The x-offset from the targeted monster where the projectile will land.
     * @param deltaY The y-offset from the targeted monster where the projectile will land.
     * @param speed The speed of the catapult projectile.
     * @param attackPower The attack power of the catapult projectile.
     */
    public CatapultProjectile(@NonNull Arena arena, @NonNull Coordinates coordinates, @NonNull Monster target, short deltaX, short deltaY, double speed, int attackPower, short damageRange){
        super(arena,coordinates,target,speed,attackPower);
        this.deltaX = deltaX;
        this.deltaY = deltaY;
        this.damageRange=damageRange;
    }

    /**
     * @see Projectile#Projectile(Arena, Monster, Projectile)
     */
    public CatapultProjectile(@NonNull Arena arena, @NonNull Monster target, @NonNull CatapultProjectile other){
        super(arena, target, other);
        this.damageRange = other.damageRange;
    }

    @Override
    public CatapultProjectile deepCopy(@NonNull Arena arena, @NonNull Monster target) {
        return new CatapultProjectile(arena, target, this);
    }

    @Override
    protected String getTowerClassName() { return "Catapult"; }

    @Override
    public void damageTarget() {
        super.damageTarget();

        short targetX = (short) (target.getX() + deltaX);
        short targetY = (short) (target.getY() + deltaY);

        arena.drawCircle(new Coordinates(targetX, targetY), damageRange);
        for (Monster m : arena.getMonsters()) {
            if (Geometry.isInCircle(m.getX(), m.getY(), targetX, targetY, damageRange)) {
                m.takeDamage(attackPower);
                System.out.println(String.format("%s@(%d, %d) -> %s@(%d, %d)", getTowerClassName(), origin.getX(), origin.getY()
                , m.getClassName(), m.getX(), m.getY()));
            }
        }
    }
}
