package project.entity;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.PriorityQueue;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.persistence.Entity;
import javax.persistence.Transient;

import project.Geometry;
import project.control.ArenaManager;
import project.query.ArenaObjectCircleSelector;
import project.query.ArenaObjectStorage.StoredType;

/**
 * Catapult can attack many monsters at the same time and has high shooting range.
 */
@Entity
public class Catapult extends Tower {

    /**
     * Returns the initial building cost of the tower.
     * @return The initial building cost of the tower.
     */
    public static int getBuildingCost() {
        return 20;
    }

    /**
     * The splash radius of Catapult which default is 25.
     */
    @Transient
    private final short splashRadius = 25;

    /**
     * Temporary list that includes the list of monsters that will be inside the splash radius.
     */
    protected LinkedList<Monster> monstersInSplashRange = new LinkedList<Monster>(); 

    /**
     * Temporary short that is the x-coordinate of the target location (NOT always the location of the target monster).
     */
    protected short targetLocationX;

    /**
     * Temporary short that is the y-coordinate of the target location (NOT always the location of the target monster).
     */
    protected short targetLocationY;

    /**
     * Constructs a newly allocated {@link Catapult} object and adds it to the currently active arena.
     * @param x The x-coordinate of the object within the storage.
     * @param y The y-coordinate of the object within the storage.
     */
    public Catapult(short x, short y) {
        super(x, y);
        this.attackPower = 25;
        this.minRange = 50;
        this.maxRange = 150;
        this.projectileSpeed = 50;
        this.reload = 20;
        this.buildValue = getBuildingCost();
        this.upgradeCost = 20;
    }

    /**
     * Returns the splash radius of catapult.
     * @return The splash radius of catapult.
     */
    public short getSplashRadius() {
        return splashRadius;
    }

    @Override
    protected void upgrade() {
        super.upgrade();
        if (this.reload <= minReload) {
            this.reload = minReload;
        } else {
            this.reload -= 1;
        }
    }

    // Catapult tries to hit the most targets in range of the main target
    @Override
    protected void shoot(PriorityQueue<Monster> validTargets) {
        LinkedList<Monster> closestTargets = new LinkedList<>();
        int closestDistance = (int) validTargets.peek().getMovementDistanceToDestination();

        while (closestDistance == (int) validTargets.peek().getMovementDistanceToDestination()) {
            closestTargets.add(validTargets.poll());
        }

        Monster target = closestTargets.peek();
        for (Monster m :closestTargets) {//every nearest monster as a center of a circle
            int count=0;//count number of monster in the circle

            for (short i = (short) (m.getX()-splashRadius); i < m.getX()+splashRadius; i++) {//square width
                for (short j = (short) (m.getY()-splashRadius); j < m.getY()+splashRadius; j++) {//square length
                    if (i < 0 || i > ArenaManager.ARENA_WIDTH) continue;
                    if (j < 0 || j > ArenaManager.ARENA_HEIGHT) continue;

                    if (Geometry.isInCircle(i,j,m.getX(),m.getY(),splashRadius)){//splash radius in current point
                        LinkedList<ArenaObject> monInCircle = storage.getQueryResult(
                                new ArenaObjectCircleSelector(i, j, splashRadius), EnumSet.of(StoredType.MONSTER));  

                        if(count < monInCircle.size()){
                            monstersInSplashRange.clear();
                            count=monInCircle.size();
                            target = m;
                            targetLocationX = i;
                            targetLocationY = j;
                            for (ArenaObject o : monInCircle) {
                                monstersInSplashRange.add((Monster) o);
                            }
                        }
                    }
                }
            }
        }

        short deltaX = (short) (targetLocationX - target.getX());
        short deltaY = (short) (targetLocationY - target.getY());
        ArenaObjectFactory.createProjectile(this, target, deltaX, deltaY);
    }

    @Override
    public String getDisplayDetails() {
        return String.format("Attack Power: %d\nSplash Radius: %d\nReload Time: %d\nRange: [%d , %d]\nUpgrade Cost: %d\nBuild Value: %d", this.attackPower,
            this.splashRadius, this.reload,this.minRange,this.maxRange,this.upgradeCost,this.buildValue);
    }

    @Override
    protected ImageView getDefaultImage() {
        return new ImageView(new Image("/catapult.png", ArenaManager.GRID_WIDTH, ArenaManager.GRID_HEIGHT, true, true));
    }
}