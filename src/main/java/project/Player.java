package project;

import project.towers.Tower;

import java.util.ArrayList;
import java.util.List;

public class Player {


    // Attribute
    private final String name;
    private int resource;

    public Player(String name, int resource) {
        this.name = name;
        this.resource = resource;
    }

    public void spendResources(int amount) {
        resource = Math.max(0,  resource - amount);
    }

}
