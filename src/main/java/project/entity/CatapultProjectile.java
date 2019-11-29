package project.entity;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Entity;

import project.control.ArenaManager;
import project.query.ArenaObjectCircleSelector;
import project.query.ArenaObjectStorage.StoredType;
import project.util.Geometry;

/**
 * Projectile created by {@link Catapult}.
 */
@Entity
public class CatapultProjectile extends Projectile {

    /**
     * The display duration of the splash effect.
     * Damage is still only applied on the first frame.
     */
    private static int SPLASH_DISPLAY_DURATION = 2;

    /**
     * The splash radius of the projectile.
     */
    private short splashRadius;

    /**
     * Default constructor.
     */
    public CatapultProjectile() {}

    /**
     * Constructs a newly allocated {@link CatapultProjectile} object.
     * @param tower The tower from which this projectile originates.
     * @param target The monster that the projectile will pursue.
     * @param deltaX The x-offset from the targeted monster where the projectile will land.
     * @param deltaY The y-offset from the targeted monster where the projectile will land.
     */
    public CatapultProjectile(Catapult tower, Monster target, short deltaX, short deltaY) {
        super(tower, target, deltaX, deltaY);
        this.splashRadius = tower.getSplashRadius();
    }

    /**
     * Returns the splash radius of the projectile.
     * @return The splash radius of the projectile.
     */
    public short getSplashRadius() {
        return splashRadius;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void damageTarget() {
        // Don't call super method to prevent double hitting
        // super()

        // The hit location
        short hitX = getX();
        short hitY = getY();
        List<Monster> monstersInSplashRange = new LinkedList<>();

        // Run the targeting algorithm on arrival to hit the most monsters next to the originally chosen target
        int count=0;//count number of monster in the circle
        double rmsDist=Double.POSITIVE_INFINITY;//To minimize the offset

        for (short i = (short) (target.getX()-splashRadius); i <= target.getX()+splashRadius; i++) {//square width
            for (short j = (short) (target.getY()-splashRadius); j <= target.getY()+splashRadius; j++) {//square length
                if (i < 0 || i > ArenaManager.ARENA_WIDTH) continue;
                if (j < 0 || j > ArenaManager.ARENA_HEIGHT) continue;

                if (Geometry.isInCircle(i,j,target.getX(),target.getY(),splashRadius)){//splash radius in current point
                    List<ArenaObject> monInCircle = storage.getQueryResult(
                            new ArenaObjectCircleSelector(i, j, splashRadius), EnumSet.of(StoredType.MONSTER));
                    if(count <= monInCircle.size()){
                        monstersInSplashRange = new LinkedList<>();
                        double thisRMSDist = 0;
                        for (ArenaObject o : monInCircle) {
                            monstersInSplashRange.add((Monster) o);
                            thisRMSDist += (i - o.getX()) * (i - o.getX()) + (j - o.getY()) * (j - o.getY());
                        }
                        thisRMSDist /= monstersInSplashRange.size();
                        thisRMSDist = Math.sqrt(thisRMSDist);

                        if (thisRMSDist < rmsDist) {
                            count=monInCircle.size();
                            hitX = i;
                            hitY = j;
                            rmsDist = thisRMSDist;
                        }
                    }
                }
            }
        }

        ArenaManager.getActiveUIController().drawCircle(hitX, hitY, splashRadius, SPLASH_DISPLAY_DURATION);

        for (ArenaObject m : monstersInSplashRange) {
            ((Monster) m).takeDamage(damage, this);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ImageView getDefaultImage() {
        return new ImageView(new Image("/catapultProjectile.png", ArenaManager.GRID_WIDTH / 2, ArenaManager.GRID_HEIGHT / 2, true, true));
    }
}
