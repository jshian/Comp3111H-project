package project.projectiles;

import project.Coordinates;
import project.monsters.Monster;

public class IceProjectile extends Projectile{
    /**
     * The slow down time to monsters of projectile.
     */
    private int slowDown;

    /**
     * Constructor for the Projectile class.
     * @param coordinates The coordinates of the pixel where the ice projectile is initially located.
     * @param target The coordinate of Monster that the projectile will pursue, which should not be <code>null</code>.
     * @param speed The speed of the ice projectile.
     * @param slowDown The cold down time of the ice projectile.
     */
    public IceProjectile(Coordinates coordinates, Coordinates target, double speed, int slowDown){
        super(coordinates,target,speed,0);
        this.slowDown=slowDown;
    }
}
