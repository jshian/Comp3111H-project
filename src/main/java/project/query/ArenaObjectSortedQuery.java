package project.query;

import java.util.LinkedList;
import java.util.PriorityQueue;

import project.entity.ArenaObject;
import project.query.ArenaObjectStorage.SortOption;
import project.query.ArenaObjectStorage.StoredComparableType;

/**
 * A sorted query on the {@link ArenaObjectStorage}.
 * @param <T> The type of comparable {@link ArenaObject}.
 */
class ArenaObjectSortedQuery<T extends ArenaObject & Comparable<T>> {

    /**
     * A list of selectors that collectively restricts the result.
     */
    protected LinkedList<ArenaObjectSortedSelector<T>> selectors = new LinkedList<>();

    /**
     * Constructs a newly allocated {@link ArenaObjectSortedQuery} object with no selectors.
     */
    public ArenaObjectSortedQuery() {}

    /**
     * Constructs a newly allocated {@link ArenaOArenaObjectSortedQuerybjectQuery} object with one selector.
     * @param selector The selector.
     */
    public ArenaObjectSortedQuery(ArenaObjectSortedSelector<T> selector) {
        this.selectors.add(selector);
    }

    /**
     * Constructs a newly allocated {@link ArenaObjectSortedQuery} object with multiple selectors.
     * @param selectors The list of selectors.
     */
    public ArenaObjectSortedQuery(LinkedList<ArenaObjectSortedSelector<T>> selectors) {
        this.selectors = new LinkedList<>(selectors);
    }

    /**
     * Adds a selector that further restricts the selection.
     * @param selector The selector to be added.
     */
    public void restrict(ArenaObjectSortedSelector<T> selector) {
        if (!selectors.contains(selector)) {
            selectors.add(selector);
        }
    }

    /**
     * Runs the sorted query on a storage.
     * @param storage The storage to run the query on.
     * @param type The type of comparable {@link ArenaObject} to select.
     * @param option The sorting option.
     * @return The query result.
     */
    PriorityQueue<T> run(ArenaObjectStorage storage, StoredComparableType type, SortOption option) {
        // Return everything if there are no selectors
        if (selectors.isEmpty()) {
            return new PriorityQueue<T>(storage.getIndexFor(type));
        }

        // Query using the minSelector and apply the other selections as the results are being fetched
        float minSelectivity = Float.POSITIVE_INFINITY;
        ArenaObjectSortedSelector<T> minSelector = null;
        for (ArenaObjectSortedSelector<T> selector : selectors) {
            float selectivity = selector.estimateSelectivity(storage);
            if (selectivity > minSelectivity) {
                minSelectivity = selectivity;
                minSelector = selector;
            }
        }

        assert (minSelector != null);

        LinkedList<ArenaObjectSortedSelector<T>> otherSelectors = new LinkedList<>(selectors);
        otherSelectors.remove(minSelector);

        return minSelector.select(storage, type, otherSelectors, option);
    }
}