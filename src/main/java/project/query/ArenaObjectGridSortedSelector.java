package project.query;

import project.control.ArenaManager;
import project.entity.ArenaObject;

/**
 * A class that selects the {@link ArenaObject}s inside a grid within the arena that contains a defined point.
 * @param <T> The type of comparable {@link ArenaObject} that is selected.
 */
public class ArenaObjectGridSortedSelector<T extends ArenaObject & Comparable<T>>
        extends ArenaObjectRectangleSortedSelector<T> {

    /**
     * Constructs a newly allocated {@link ArenaObjectGridSortedSelector} object.
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     */
    public ArenaObjectGridSortedSelector(short x, short y) {
        super(ArenaManager.getGridLeftXFromCoor(x), ArenaManager.getGridTopYFromCoor(y), ArenaManager.GRID_WIDTH, ArenaManager.GRID_HEIGHT);
    }

}