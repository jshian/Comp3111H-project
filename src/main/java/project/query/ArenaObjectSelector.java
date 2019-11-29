package project.query;

import java.util.EnumSet;
import java.util.List;

import project.entity.ArenaObject;
import project.query.ArenaObjectStorage.StoredType;

/**
 * Interface for classes that perform selection on an {@link ArenaObjectStorage}.
 */
interface ArenaObjectSelector {

    /**
     * Estimates the number of accesses of the selector, disregarding the type filter.
     * @param storage The storage to run the select from.
     * @return The estimated number of accesses.
     */
    abstract int estimateCost(ArenaObjectStorage storage);

    /**
     * Performs selection on a storage.
     * @param storage The storage to run the select from.
     * @param types The types of {@link ArenaObject} to select.
     * @param filters Other selectors to filter the results during the selection, as optimization.
     * @return The selection result. Returns an empty collection if nothing is found.
     */
    abstract List<ArenaObject> select(ArenaObjectStorage storage,
            EnumSet<StoredType> types, List<ArenaObjectSelector> filters);

    /**
     * Returns whether an object passes through a set of filters.
     * @param o The object to test.
     * @param types The types of {@link ArenaObject} to select.
     * @param filters The set of filters.
     * @return Whether an object passes through all of the filters.
     */
    default boolean isAllSatisfied(ArenaObject o, EnumSet<StoredType> types,
            List<ArenaObjectSelector> filters) {

        boolean satisfiesType = false;
        for (StoredType type : types) {
            if (type.getObjectClass().isAssignableFrom(o.getClass())) {
                satisfiesType = true;
                break;
            }
        }
        if (!satisfiesType) return false;

        for (ArenaObjectSelector s : filters) {
            if (!s.isInSelection(o)) return false;
        }

        return true;
    }

    /**
     * Returns whether an object satisfies the selection.
     * @param o The object to test.
     * @return Whether the object satisfies the selection.
     */
    abstract boolean isInSelection(ArenaObject o);

    /**
     * For testing only. Returns whether an object satisfies the selection by definition.
     * @param o The object to test.
     * @return Whether the object satisfies the selection the selection by definition.
     */
    abstract boolean isInSelectionByDefinition(ArenaObject o);

}