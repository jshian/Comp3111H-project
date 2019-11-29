package project.query;

import java.util.LinkedList;
import java.util.List;
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
    @SuppressWarnings("unchecked")
    List<T> run(ArenaObjectStorage storage, StoredComparableType type, SortOption option) {
        // Return everything if there are no selectors
        if (selectors.isEmpty()) {
            return storage.getSortedIndexFor(type, option);
        }

        // The cost of accessing through type-based index
        int typeIndexCost = storage.getSortedIndexFor(type, option).size();

        // The cost of accessing through the selector with the least cost
        int minCost = Integer.MAX_VALUE;
        ArenaObjectSortedSelector<T> minSelector = null;
        for (ArenaObjectSortedSelector<T> selector : selectors) {
            int cost = selector.estimateCost(storage);
            if (cost < minCost) {
                minCost = cost;
                minSelector = selector;
            }
        }

        if (typeIndexCost <= minCost) {
            // Query using the type index and apply each selection as the results are being fetched
            List<T> result = new LinkedList<>();
            ArenaObjectPropertySortedSelector<T> dummySelector = new ArenaObjectPropertySortedSelector<T>(type.getObjectClass(), o -> true);

            for (ArenaObject o : storage.getSortedIndexFor(type, option)) {
                if (dummySelector.isComparableAndAllSatisfied(o, type, selectors)) {
                    result.add((T) o);
                }
            }

            return result;
        } else {
            // Query using the minSelector and apply the other selections as the results are being fetched
            List<ArenaObjectSortedSelector<T>> otherSelectors = new LinkedList<>(selectors);
            otherSelectors.remove(minSelector);

            return minSelector.select(storage, type, otherSelectors, option);
        }
    }
}