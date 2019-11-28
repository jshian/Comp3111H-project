package project.field;

import java.util.LinkedList;

import org.checkerframework.checker.nullness.qual.Nullable;

import project.control.ArenaManager;
import project.entity.ArenaObjectPositionInfo;

/**
 * Interface for scalar field thats permeates the arena.
 */
public interface ArenaScalarField<T extends Number & Comparable<T>> {

    /**
     * Represents a point on an {@link ArenaScalarField}.
     */
    public class ScalarFieldPoint {
        private short x;
        private short y;

        /**
         * Constructs a newly allocated {@link ScalarFieldPoint} object.
         * @param x The x-coordinate of the point.
         * @param y The y-coordinate of the point.
         */
        ScalarFieldPoint(short x, short y) {
            ArenaObjectPositionInfo.assertValidPosition(x, y);
            this.x = x;
            this.y = y;
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
    }

    /**
     * Returns the value of the scalar field at a given point.
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     * @return The value of the scalar field at the point.
     */
    public abstract T getValueAt(short x, short y);

    /**
     * Sets the value of the scalar field at a given point.
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     * @param value The new value at the point.
     */
    abstract void setValueAt(short x, short y, T value);

    /**
     * Sets the value at each point on the scalar field.
     * @param value The new value.
     */
    abstract void setAll(T value);

    /**
     * Returns the immediate neighbour in each of the four cardinal directions from the given point, if they exist.
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     * @return The neighbours which satisfy the requirement, or <code>null</code> if the given point is the only point in the entire field.
     */
    static @Nullable LinkedList<ScalarFieldPoint> getTaxicabNeighbours(short x, short y) {
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
    public default @Nullable ScalarFieldPoint descendTaxicab(short x, short y) {
        ArenaObjectPositionInfo.assertValidPosition(x, y);
        
        if (getTaxicabNeighbours(x, y) == null) return null;

        T lowestCost = getValueAt(x, y);
        ScalarFieldPoint lowestCostNeighbour = null;
        for (ScalarFieldPoint neighbour : getTaxicabNeighbours(x, y)) {
            T cost = getValueAt(neighbour.x, neighbour.y);

            if (cost.compareTo(lowestCost) < 0) {
                lowestCost = cost;
                lowestCostNeighbour = neighbour;
            }
        }

        return lowestCostNeighbour;
    }
}