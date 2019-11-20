package project.arena.towers;

import javax.persistence.Entity;

import javafx.scene.image.Image;
import org.checkerframework.checker.nullness.qual.NonNull;

import javafx.scene.image.ImageView;
import project.UIController;
import project.arena.Arena;
import project.arena.Coordinates;
import project.arena.monsters.Monster;
import project.arena.projectiles.Projectile;

/**
 * IceTower slow down the speed of monster without damage.
 */
@Entity
public class IceTower extends Tower {

    /**
     * Finds the initial building cost of the tower.
     * @return The initial building cost of the tower.
     */
    public static int findInitialBuildingCost() {
        return 15;
    }

    /**
     * The maximum slow down duration of the tower.
     */
    private final int maxSlowDownTime = 100;

    /**
     * The current slow down duration of ice tower. It cannot go beyond {@link #maxSlowDownTime}.
     */
    private int slowDownTime = 1;

    /**
     * Constructor of ice tower.
     * @param arena The arena to attach the tower to.
     * @param coordinates The coordinates of ice tower.
     */
    public IceTower(@NonNull Arena arena, @NonNull Coordinates coordinates){
        super(arena, coordinates);
        this.attackPower = 0;
        this.buildValue = findInitialBuildingCost();
        this.maxShootingRange = 50;
        this.slowDownTime = 10;
        this.projectileSpeed = 10;
        this.upgradeCost = 10;
        this.imageView = new ImageView(new Image("/iceTower.png", UIController.GRID_WIDTH, UIController.GRID_HEIGHT, true, true));
        this.coordinates.bindByImage(this.imageView);
    }

    /**
     * Constructor of ice tower.
     * @param arena The arena to attach the tower to.
     * @param coordinates The coordinates of ice tower.
     * @param imageView The image view of ice tower.
     */
    public IceTower(@NonNull Arena arena, @NonNull Coordinates coordinates, ImageView imageView) {
        super(arena, coordinates, imageView);
        this.attackPower = 0;
        this.buildValue = 15;
        this.maxShootingRange = 50;
        this.slowDownTime = 10;
        this.projectileSpeed = 10;
        this.upgradeCost = 10;
    }

    /**
     * @see Tower#Tower(Arena, Tower)
     */
    public IceTower(@NonNull Arena arena, @NonNull IceTower other) {
        super(arena, other);
        this.slowDownTime = other.slowDownTime;
    }

    @Override
    public IceTower deepCopy(@NonNull Arena arena) {
        return new IceTower(arena, this);
    }

    @Override
    protected String getClassName() { return "Ice Tower"; }

    @Override
    protected void upgrade() {
        super.upgrade();
        if (this.slowDownTime + 5 >= maxSlowDownTime) {
            this.slowDownTime = maxSlowDownTime;
        } else {
            this.slowDownTime += 5;
        }
    }

    @Override
    public Projectile generateProjectile(){
        if(!isReload()) {
            for (Monster m : arena.getMonsters()) {
                if (isValidTarget(m)) {
                    this.hasAttack = true;
                    this.counter = this.reload;
                    return arena.createProjectile(this, m, (short) 0, (short) 0);
                }
            }
        }
        return null;
    }

    /**
     * Accesses the slow down time of tower.
     * @return the slow down time of tower.
     */
    public final int getSlowDownTime() { return slowDownTime; }

    /**
     * Accesses the information of tower.
     * @return the information of tower.
     */
    @Override
    public String getInformation() {
        return String.format("Slow Duration: %d\nReload Time: %d\nRange: [%d , %d]\nUpgrade Cost: %d\nBuild Value: %d", this.slowDownTime,
            this.reload,this.minShootingRange,this.maxShootingRange,this.upgradeCost,this.buildValue);
    }
}
