package project.projectiles;

import project.Coordinates;
import project.monsters.Monster;

public class CatapultProjectile extends Projectile{

    /**
     * Constructor for the Projectile class.
     * @param coordinates The coordinates of the pixel where the catapult projectile is initially located.
     * @param speed The speed of the catapult projectile.
     * @param attackPower The attack power of the catapult projectile.
     */
    public CatapultProjectile(Coordinates coordinates, Coordinates target,double speed, int attackPower){
        super(coordinates,target,speed,attackPower);
    }

}
