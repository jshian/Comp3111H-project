package project.entity;

import javax.persistence.Entity;

import javafx.scene.image.Image;
import org.checkerframework.checker.nullness.qual.NonNull;

import javafx.scene.image.ImageView;
import project.UIController;
import project.arena.ArenaInstance;
import project.arena.Coordinates;

/**
 * Basic tower has no any special ability.
 */
@Entity
public class BasicTower extends Tower {

    /**
     * Finds the initial building cost of the tower.
     * @return The initial building cost of the tower.
     */
    public static int findInitialBuildingCost() {
        return 10;
    }

    /**
     * Constructor of basic tower.
     * @param arena The arena to attach the tower to.
     * @param coordinates The coordinate of basic tower.
     */
    public BasicTower(ArenaInstance arena, Coordinates coordinates){
        super(arena, coordinates);
        this.attackPower = 10;
        this.buildValue = findInitialBuildingCost();
        this.maxShootingRange = 65;
        this.projectileSpeed = 5;
        this.upgradeCost = 10;
        this.imageView = new ImageView(new Image("/basicTower.png", UIController.GRID_WIDTH, UIController.GRID_HEIGHT, true, true));
        this.coordinates.bindByImage(this.imageView);
    }

    /**
     * Constructor of basic tower.
     * @param arena The arena to attach the tower to.
     * @param coordinates The coordinates of the tower.
     * @param imageView The image view of the tower.
     */
    public BasicTower(ArenaInstance arena, Coordinates coordinates, ImageView imageView) {
        super(arena, coordinates, imageView);
        this.attackPower = 10;
        this.buildValue = 10;
        this.maxShootingRange = 65;
        this.projectileSpeed = 5;
        this.upgradeCost = 10;
    }

    /**
     * @see Tower#Tower(ArenaInstance, Tower)
     */
    public BasicTower(ArenaInstance arena, BasicTower other) {
        super(arena, other);
    }

    @Override
    public BasicTower deepCopy(ArenaInstance arena) {
        return new BasicTower(arena, this);
    }

    @Override
    protected String getClassName() { return "Basic Tower"; }

    @Override
    protected void upgrade() {
        super.upgrade();
        if (this.attackPower + 5 >= maxAttackPower) {
            this.attackPower = maxAttackPower;
        } else {
            this.attackPower += 5;
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
}
