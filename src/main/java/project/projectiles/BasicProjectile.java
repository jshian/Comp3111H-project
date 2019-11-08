package project.projectiles;

import project.Arena;
import project.Coordinates;
import project.monsters.Monster;

import java.util.EnumSet;
import java.util.LinkedList;

public class BasicProjectile extends Projectile{

    /**
     * Constructor for the Projectile class.
     * @param arena The arena the projectile is attached to.
     * @param coordinates The coordinates of the pixel where the projectile is initially located.
     * @param target The Monster that the projectile will pursue, which should not be <code>null</code>.
     * @param speed The speed of the projectile.
     * @param attackPower The attack power of the projectile.
     */
    public BasicProjectile(Arena arena, Coordinates coordinates, Coordinates target, double speed, int attackPower) {
        super(arena,coordinates,target,speed,attackPower);
    }

    @Override
    public void moveOneFrame() {
        super.moveOneFrame();
        if (hasReachedTarget()){
            LinkedList<Arena.ExistsInArena> targets = arena.findObjectsInRange(target, tower.getMaxShootingRange(),EnumSet.of(Arena.TypeFilter.Monster));
            if(targets.size()>0){
                Monster target = (Monster)targets.get(0);
                if (target != null) {
                    target.setHealth(target.getHealth() - attackPower);
                    System.out.println(String.format("Basic Tower@(%d,%d) -> %s@(%d,%d)", tower.getX(), tower.getY()
                            , target.getClassName(), target.getX(), target.getY()));
                }
            }
        }
    }
}
