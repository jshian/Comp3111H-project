package project;

import javax.persistence.*;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import project.arena.ArenaEventRegister;
import project.arena.ArenaInstance;
import project.entity.ArenaObject;
import project.entity.Monster;
import project.entity.Projectile;
import project.entity.Tower;
import project.event.EventHandler;
import project.event.eventargs.ArenaObjectEventArgs;
import project.ui.UIController;


@Entity(name="Player")
@Access(AccessType.PROPERTY)
public class Player {

    /**
     * ID for storage using Java Persistence API
     */
    private Long id;

    /**
     * Name of the player.
     */
    private String name = "";

    /**
     * Resources that the player has.
     */
    private IntegerProperty resources = new SimpleIntegerProperty(0);

    /**
     * Score of the player.
     */
    private IntegerProperty score = new SimpleIntegerProperty(0);

    /**
     * The method invoked when an {@link ArenaObject} is being added.
     */
    @Transient
    private EventHandler<ArenaObjectEventArgs> onAddObject = (sender, args) -> {
        ArenaObject subject = args.subject;

        if (sender instanceof UIController && subject instanceof Tower) {
            spendResources(((Tower) subject).getBuildValue());
        }
    };

    /**
     * The method invoked when an {@link ArenaObject} is being removed.
     */
    @Transient
    private EventHandler<ArenaObjectEventArgs> onRemoveObject = (sender, args) -> {
        ArenaObject subject = args.subject;

        if (sender instanceof UIController && subject instanceof Tower) {
            receiveResources(((Tower) subject).getBuildValue() / 2);
        }

        if (sender instanceof Projectile && subject instanceof Monster) {
            int resourceValue = ((Monster) subject).getResourceValue();
            receiveResources(resourceValue);
            score.set(score.get() + resourceValue);
        }
    };

    /**
     * Default constructor.
     */
    public Player() {}

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
        register.ARENA_OBJECT_ADD.subscribe(onAddObject);
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
     * get score Property of player.
     * @return scores Property of player.
     */
    public IntegerProperty scoreProperty() {
        return score;
    }

    /**
     * get the score of player.
     * @return the score of player.
     */
    public int getScore() {
        return score.get();
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

    /**
     * Reduce resources of player.
     * @param amount amount of resources to reduce.
     */
    public void spendResources(int amount) {
        Platform.runLater(() -> {
            resources.set(Math.max(0, resources.get() - amount));
        });
    }

    /**
     * Increase resources of player.
     * @param amount amount of resources to increase.
     */
    public void receiveResources(int amount) {
        Platform.runLater(() -> {
            resources.set(resources.get() + amount);
        });
    }

    // getter/setter for jpa.

    /**
     * access the id of the player.
     * @return the id of the player.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long getId() {
        return id;
    }

    /**
     * set the id of the player.
     */
    private void setId(Long id) {
        this.id = id;
    }

    /**
     * set the name of the player.
     */
    private void setName(String name) {
        this.name = name;
    }

    /**
     * set the resources of the player.
     * @return the resources of the player.
     */
    private void setResources(int resources) {
        this.resources.set(resources);
    }

    /**
     * set the score of the player.
     * @return the score of the player.
     */
    private void setScore(int score) {
        this.score.set(score);
    }
}
