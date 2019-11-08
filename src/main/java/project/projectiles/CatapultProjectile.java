package project.projectiles;

import javafx.scene.shape.Circle;
import project.*;
import project.monsters.Monster;
import project.towers.Catapult;
import project.towers.Tower;

import java.util.EnumSet;
import java.util.LinkedList;

public class CatapultProjectile extends Projectile{

    /**
     * Constructor for the Projectile class.
     * @param arena The arena the projectile is attached to.
     * @param coordinates The coordinates of the pixel where the catapult projectile is initially located.
     * @param target The Coordinates of Monster that the projectile will pursue, which should not be <code>null</code>.
     * @param speed The speed of the catapult projectile.
     * @param attackPower The attack power of the catapult projectile.
     */
    public CatapultProjectile(Arena arena, Coordinates coordinates, Coordinates target,double speed, int attackPower){
        super(arena,coordinates,target,speed,attackPower);
    }

    @Override
    public void moveOneFrame() {
        super.moveOneFrame();
        if (hasReachedTarget()){
            //draw damage circle
            arena.drawCircle(target,((Catapult)tower).getDamageRange());

            //give damage
            LinkedList<Arena.ExistsInArena> monsters = arena.findObjectsInRange(target, ((Catapult)tower).getDamageRange(), EnumSet.of(Arena.TypeFilter.Monster));
            for (Arena.ExistsInArena monster : monsters) {
                if (monster instanceof Monster) {
                    ((Monster) monster).setHealth(((Monster) monster).getHealth() - attackPower);
                    System.out.println(String.format("Catapult@(%d,%d) -> %s@(%d,%d)", tower.getX(), tower.getY()
                            , ((Monster) monster).getClassName(), monster.getX(), monster.getY()));
                }
            }
        }
    }
}
