package project;

import javax.persistence.Entity;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import project.controller.ArenaEventRegister;
import project.controller.ArenaManager;
import project.entity.ArenaObject;
import project.entity.Monster;
import project.entity.Tower;
import project.event.EventHandler;
import project.event.eventargs.ArenaObjectEventArgs;
import project.event.eventargs.ArenaTowerEventArgs;
import project.event.eventargs.BooleanResultEventArgs;

@Entity
public class Player {

    // Attribute
    private final String name;
    private IntegerProperty resources = new SimpleIntegerProperty(0);

    /**
     * The method invoked when an {@link ArenaObject} is being added.
     */
    private EventHandler<ArenaObjectEventArgs> onAddObject = (sender, args) -> {
        ArenaObject subject = args.subject;

        if (subject instanceof Tower) {
            spendResources(((Tower) subject).getBuildValue());
        }
    };

    /**
     * The method invoked when an {@link ArenaObject} is being removed.
     */
    private EventHandler<ArenaObjectEventArgs> onRemoveObject = (sender, args) -> {
        ArenaObject subject = args.subject;

        if (subject instanceof Tower) {
            receiveResources(((Tower) subject).getBuildValue() / 2);
        }
        if (subject instanceof Monster) {
            receiveResources(((Monster) subject).getResourceValue());
        }
    };

    /**
     * The method invoked when a result is returned from checking whether a tower could be upgraded.
     */
    private EventHandler<BooleanResultEventArgs> onGetCheckTowerUpgradeResult = (sender, args) -> {
        if (args.recipient != this) return;

        //TODO
    };

    /**
     * Constructs a newly allocated {@link Player} object.
     * @param name The name of the player.
     * @param resource The amount of resources the player has.
     */
    public Player(String name, int resource) {
        this.name = name;
        this.resources.set(resource);

        ArenaEventRegister register = ArenaManager.getActiveEventRegister();
        register.ARENA_OBJECT_ADD.subscribe(onAddObject);
        register.ARENA_OBJECT_REMOVE.subscribe(onRemoveObject);
        register.ARENA_TOWER_UPGRADE_CHECK_RESULT.subscribe(onGetCheckTowerUpgradeResult);
    }

    /**
     * get the name of player.
     * @return the amount of resources of player.
     */
    public String getName() { return name; }

    /**
     * get the amount of resources of player.
     * @return the amount of resources of player.
     */
    public int getResources() {
        return resources.get();
    }

    /**
     * get resources Property of player.
     * @return resources Property of player.
     */
    public IntegerProperty resourcesProperty() {
        return resources;
    }

    /**
     * check if the player has enough resources to perform the action.
     * @param cost cost of an action.
     * @return true if the player has enough resources or false otherwise.
     */
    public boolean hasResources(int cost)
    {
        if (cost > resources.get()) {
            return false;
        } else {
            return true;
        }
    }

    public void spendResources(int amount) {
        resources.set(Math.max(0,  resources.get() - amount));
    }

    public void receiveResources(int amount) {
        resources.set(resources.get() + amount);
    }

}
