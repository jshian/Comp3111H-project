package project.query;

import project.control.ArenaManager;

/**
 * A class that selects the {@link ArenaObject}s inside a grid within the arena that contains a defined point.
 */
public class ArenaObjectGridSelector extends ArenaObjectRectangleSelector {

    /**
     * Constructs a newly allocated {@link ArenaObjectGridSelector} object.
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     */
    public ArenaObjectGridSelector(short x, short y) {
        super(ArenaManager.getGridLeftXFromCoor(x), ArenaManager.getGridTopYFromCoor(y), ArenaManager.GRID_WIDTH, ArenaManager.GRID_HEIGHT);
    }

}