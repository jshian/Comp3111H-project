package project;

import javax.persistence.Entity;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

@Entity
public class Player {

    // Attribute
    private final String name;
    private IntegerProperty resources = new SimpleIntegerProperty(0);

    /**
     * Constructor of player
     * @param name the name of player
     * @param resource the amount of resources player has.
     */
    public Player(String name, int resource) {
        this.name = name;
        this.resources.set(resource);
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
