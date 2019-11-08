package project.projectiles;

import project.*;

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

}
