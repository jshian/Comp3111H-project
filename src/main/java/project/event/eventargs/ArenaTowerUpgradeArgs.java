package project.event.eventargs;

import project.entity.Tower;

/**
 * Struct containing data of a {@link Tower} before and after an upgrade..
 */
public abstract class ArenaTowerUpgradeArgs extends EventArgs {

    /**
     * The tower before the upgrade.
     */
    public Tower originalTower;

    /**
     * The tower after the upgrade.
     */
    public Tower newTower;
}