package project.projectiles;

import project.*;
import project.monsters.Monster;

public class IceProjectile extends Projectile{
    /**
     * The slow down time to monsters of projectile.
     */
    private int slowDown;

    /**
     * Constructor for the Projectile class.
     * @param arena The arena the projectile is attached to.
     * @param coordinates The coordinates of the pixel where the ice projectile is initially located.
     * @param target The Coordinates of Monster that the projectile will pursue, which should not be <code>null</code>.
     * @param speed The speed of the ice projectile.
     * @param slowDown The cold down time of the ice projectile.
     */
    public IceProjectile(Arena arena, Coordinates coordinates, Coordinates target, double speed, int slowDown){
        super(arena,coordinates,target,speed,0);
        this.slowDown=slowDown;
    }

    /**
     * get slow down time of projectile.
     * @return slow down time of projectile.
     */
    public int getSlowDown() { return slowDown; }
}
