package project.field;

import java.util.Arrays;
import java.util.LinkedList;

import org.checkerframework.checker.nullness.qual.Nullable;

import project.controller.ArenaManager;
import project.entity.ArenaObjectPositionInfo;

/**
 * A scalar field that permeates the arena.
 */
public class ArenaScalarField<T extends Number & Comparable<T>> {

    private Object[][] values = new Object[ArenaManager.ARENA_WIDTH][ArenaManager.ARENA_HEIGHT];

    /**
     * Represents a point on an {@link ArenaScalarField}.
     */
    public class ScalarFieldPoint {
        private short x;
        private short y;
        private T value;

        @SuppressWarnings("unchecked") 
        protected ScalarFieldPoint(short x, short y) {
            ArenaObjectPositionInfo.assertValidPosition(x, y);
            this.x = x;
            this.y = y;
            this.value = (T) values[x][y];
        }

        /**
         * Returns the x-coordinate of the point.
         * @return The x-coordinate of the point.
         */
        public short getX() { return x; }

        /**
         * Returns the y-coordinate of the point.
         * @return The y-coordinate of the point.
         */
        public short getY() { return y; }

        /**
         * Returns the value of the scalar field at the point.
         * @return The value of the scalar field at the point.
         */
        public Number getValue() { return value; }
    }

    /**
     * Returns the value of the scalar field at a given point.
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     * @return The value of the scalar field at the point.
     */
    @SuppressWarnings("unchecked") 
    public T getValueAt(short x, short y) {
        ArenaObjectPositionInfo.assertValidPosition(x, y);
        return (T) values[x][y];
    }

    /**
     * Sets the value at each point on the scalar field.
     * @param value The new value.
     */
    protected final void setAll(T value) {
        Arrays.fill(values, value);
    }

    /**
     * Sets the value of the scalar field at a given point.
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     * @param value The new value at the point.
     */
    protected final void setValueAt(short x, short y, T value) {
        ArenaObjectPositionInfo.assertValidPosition(x, y);
        values[x][y] = value;
    }

    /**
     * Returns the immediate neighbour in each of the four cardinal directions from the given point, if they exist.
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     * @return The neighbours which satisfy the requirement, or <code>null</code> if the given point is the only point in the entire field.
     */
    protected final @Nullable LinkedList<ScalarFieldPoint> getTaxicabNeighbours(short x, short y) {
        LinkedList<ScalarFieldPoint> neighbours = new LinkedList<>();

        // Left
        if (x > 0) neighbours.add(new ScalarFieldPoint((short) (x - 1), y));

        // Right
        if (x < ArenaManager.ARENA_WIDTH) neighbours.add(new ScalarFieldPoint((short) (x + 1), y));

        // Up
        if (y > 0) neighbours.add(new ScalarFieldPoint(x, (short) (y - 1)));

        // Down
        if (y < ArenaManager.ARENA_HEIGHT) neighbours.add(new ScalarFieldPoint(x, (short) (y + 1)));

        return neighbours;
    }

    /**
     * Performs gradient descent in one of the four cardinal directions from the given point.
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     * @return The point after moving by one step, or <code>null</code> if the current point is a local minimum.
     */
    public @Nullable ScalarFieldPoint descendTaxicab(short x, short y) {
        ArenaObjectPositionInfo.assertValidPosition(x, y);

        T lowestCost = getValueAt(x, y);
        ScalarFieldPoint lowestCostNeighbour = null;
        for (ScalarFieldPoint neighbour : getTaxicabNeighbours(x, y)) {
            T cost = neighbour.value;

            if (cost.compareTo(lowestCost) < 0) {
                lowestCost = cost;
                lowestCostNeighbour = neighbour;
            }
        }

        return lowestCostNeighbour;
    }
}