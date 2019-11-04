package project;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Grids are arranged in a tabular manner in the Arena and limit the positioning
 * of Towers. Grids share a similar yet distinct positioning system from other
 * objects in the Arena, which use the {@link Coordinates} class.
 * 
 * @see Arena
 * @see Tower
 */
class Grid {
    /**
     * The horizontal position of the grid, with 0 denoting the left-most grid increasing towards the right.
     */
    private final int xPos;

    /**
     * The vertical position of the grid, with 0 denoting the top-most grid, increasing towards the bottom.
     */
    private final int yPos;

    /**
     * Constructor for the Grid class.
     * @param xPos The horizontal position of the grid, with 0 denoting the left-most grid increasing towards the right.
     * @param yPos The vertical position of the grid, with 0 denoting the top-most grid, increasing towards the bottom.
     * @exception IllegalArgumentException Either x or y is outside the arena.
     */
    Grid(int xPos, int yPos) {
        if (xPos < 0 || xPos >= UIController.MAX_H_NUM_GRID)
            throw new IllegalArgumentException(
                String.format("The parameter 'x' is out of bounds. It should be between 0 and %d.", UIController.MAX_H_NUM_GRID - 1));
        if (yPos < 0 || yPos >= UIController.MAX_V_NUM_GRID)
            throw new IllegalArgumentException(
                String.format("The parameter 'y' is out of bounds. It should be between 0 and %d.", UIController.MAX_V_NUM_GRID - 1));

        this.xPos = xPos;
        this.yPos = yPos;
    }
    
    /**
     * Accesses the horizontal position.
     * @return The horizontal position, as defined in {@link Grid#Grid()}.
     */
    int getXPos() { return xPos; }

    /**
     * Accesses the vertical position.
     * @return The vertical position, as defined in {@link Grid#Grid()}.
     */
    int getYPos() { return yPos; }

    /**
     * Accesses the coordinates of the center of the grid.
     * @return The coordinates of the center of the grid.
     */
    Coordinates getCenterCoordinates() {
        return new Coordinates((int) ((xPos + 0.5) * UIController.GRID_WIDTH), (int) ((yPos + 0.5) * UIController.GRID_HEIGHT));
    }

    /**
     * Finds the grid which encloses the specified set of coordinates.
     * @param coordinates The set of coordinates.
     * @return The grid which encloses the specified set of coordinates.
     */
    static Grid findGrid(@NonNull Coordinates coordinates) {
        return new Grid(coordinates.getX() / UIController.GRID_WIDTH, coordinates.getY() / UIController.GRID_HEIGHT);
    }
}