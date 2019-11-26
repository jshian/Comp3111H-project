package project;

import javax.persistence.Entity;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import project.arena.ArenaEventRegister;
import project.arena.ArenaInstance;
import project.entity.ArenaObject;
import project.entity.Monster;
import project.entity.Projectile;
import project.event.EventHandler;
import project.event.eventargs.ArenaObjectEventArgs;

@Entity
public class Player {

    // Attribute
    private final String name;
    private IntegerProperty resources = new SimpleIntegerProperty(0);

    /**
     * The method invoked when an {@link ArenaObject} is being removed.
     */
    private EventHandler<ArenaObjectEventArgs> onRemoveObject = (sender, args) -> {
        ArenaObject subject = args.subject;

        if (sender instanceof Projectile && subject instanceof Monster) {
            receiveResources(((Monster) subject).getResourceValue());
        }
    };

    /**
     * Constructs a newly allocated {@link Player} object.
     * @param name The name of the player.
     * @param resource The amount of resources the player has.
     */
    public Player(String name, int resource) {
        this.name = name;
        this.resources.set(resource);
    }

    /**
     * Attaches the player to an arena instance so that it can receive events.
     * @param arenaInstance The arena instanceto attach to.
     */
    public void attachToArena(ArenaInstance arenaInstance) {
        ArenaEventRegister register = arenaInstance.getEventRegister();
        register.ARENA_OBJECT_REMOVE.subscribe(onRemoveObject);
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
        Platform.runLater(() -> resources.set(Math.max(0, resources.get() - amount)));
    }

    public void receiveResources(int amount) {
        Platform.runLater(() -> resources.set(resources.get() + amount));
    }

}
