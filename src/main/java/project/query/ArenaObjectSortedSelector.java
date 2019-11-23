package project.query;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.PriorityQueue;

import project.entity.ArenaComparableObject;
import project.entity.ArenaObject;
import project.query.ArenaObjectStorage.SortOption;
import project.query.ArenaObjectStorage.StoredComparableType;

/**
 * Class that performs sorted selection on an {@link ArenaObjectStorage}.
 */
abstract class ArenaObjectSortedSelector extends ArenaObjectSelector {

    /**
     * Performs sorted selection on a storage.
     * 
     * @param storage The storage to run the select from.
     * @param types   The types of {@link ArenaComparableObject} to select.
     * @param filters Other selectors to filter the results during the selection, as optimization.
     * @param option  The sorting option.
     * @return The selection result. Returns an empty collection if nothing is found.
     */
    abstract PriorityQueue<ArenaComparableObject> select(ArenaObjectStorage storage,
            EnumSet<StoredComparableType> types, LinkedList<ArenaObjectSortedSelector> filters, SortOption option);

    /**
     * Returns a new {@link PriorityQueue} based on the sorting option.
     * @param option The sorting option.
     * @return A new {@link PriorityQueue} based on the sorting option.
     */
    protected final PriorityQueue<ArenaComparableObject> createPriorityQueue(SortOption option) {
        switch (option) {
            case ASCENDING: return new PriorityQueue<>();
            case DESCENDING: return new PriorityQueue<>((o1, o2) -> o2.compareTo(o1));
        }

        return null;
    }

    /**
     * Returns whether an object is an {@link ArenaComparableObject} and passes through a set of filters.
     * @param o The object to test.
     * @param filters The set of filters.
     * @return Whether an object is an {@link ArenaComparableObject} passes through all of the filters.
     */
    protected final boolean isComparableAndAllSatisfied(ArenaObject o, EnumSet<StoredComparableType> types,
            LinkedList<ArenaObjectSortedSelector> filters) {

        if (!(o instanceof ArenaComparableObject)) return false;

        boolean satisfiesType = false;
        for (StoredComparableType type : types) {
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

}