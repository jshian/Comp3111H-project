package project.query;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.PriorityQueue;

import project.entity.ArenaComparableObject;
import project.query.ArenaObjectStorage.SortOption;
import project.query.ArenaObjectStorage.StoredComparableType;

/**
 * A sorted query on the {@link ArenaObjectStorage}.
 */
class ArenaObjectSortedQuery {

    /**
     * A list of selectors that collectively restricts the result.
     */
    protected LinkedList<ArenaObjectSortedSelector> selectors = new LinkedList<>();

    /**
     * Constructs a newly allocated {@link ArenaObjectSortedQuery} object with no selectors.
     */
    public ArenaObjectSortedQuery() {}

    /**
     * Constructs a newly allocated {@link ArenaOArenaObjectSortedQuerybjectQuery} object with one selector.
     * @param selector The selector.
     */
    public ArenaObjectSortedQuery(ArenaObjectSortedSelector selector) {
        this.selectors.add(selector);
    }

    /**
     * Constructs a newly allocated {@link ArenaObjectSortedQuery} object with multiple selectors.
     * @param selectors The list of selectors.
     */
    public ArenaObjectSortedQuery(LinkedList<ArenaObjectSortedSelector> selectors) {
        this.selectors = new LinkedList<>(selectors);
    }

    /**
     * Adds a selector that further restricts the selection.
     * @param selector The selector to be added.
     */
    public void restrict(ArenaObjectSortedSelector selector) {
        if (!selectors.contains(selector)) {
            selectors.add(selector);
        }
    }

    /**
     * Runs the sorted query on a storage.
     * @param storage The storage to run the query on.
     * @param types The types of {@link ArenaComparableObject} to select.
     * @param option The sorting option.
     * @return The query result.
     */
    PriorityQueue<ArenaComparableObject> run(ArenaObjectStorage storage, EnumSet<StoredComparableType> types, SortOption option) {
        // Return everything if there are no selectors
        if (selectors.isEmpty()) {
            LinkedList<ArenaComparableObject> result = new LinkedList<>();

            for (StoredComparableType type : types) {
                result.addAll(storage.getIndexFor(type));
            }

            return new PriorityQueue<ArenaComparableObject>(result);
        }

        // Query using the minSelector and apply the other selections as the results are being fetched
        float minSelectivity = Float.POSITIVE_INFINITY;
        ArenaObjectSortedSelector minSelector = null;
        for (ArenaObjectSortedSelector selector : selectors) {
            float selectivity = selector.estimateSelectivity(storage);
            if (selectivity > minSelectivity) {
                minSelectivity = selectivity;
                minSelector = selector;
            }
        }

        assert (minSelector != null);

        LinkedList<ArenaObjectSortedSelector> otherSelectors = new LinkedList<>(selectors);
        otherSelectors.remove(minSelector);

        return minSelector.select(storage, types, otherSelectors, option);
    }
}