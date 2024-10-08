package project.query;

import java.util.List;

import project.entity.ArenaObject;
import project.query.ArenaObjectStorage.SortOption;
import project.query.ArenaObjectStorage.StoredComparableType;

/**
 * Interface for classes that perform sorted selection on an {@link ArenaObjectStorage}.
 * @param <T> The type of comparable {@link ArenaObject}.
 */
interface ArenaObjectSortedSelector<T extends ArenaObject & Comparable<T>> extends ArenaObjectSelector {

    /**
     * Performs sorted selection on a storage.
     * @param storage The storage to run the select from.
     * @param type The type of comparable {@linkArenaObject} to select.
     * @param filters Other selectors to filter the results during the selection, as optimization.
     * @param option The sorting option.
     * @return The selection result. Returns an empty collection if nothing is found.
     */
    abstract List<T> select(ArenaObjectStorage storage, StoredComparableType type,
            List<ArenaObjectSortedSelector<T>> filters, SortOption option);

    /**
     * Returns whether an {@link ArenaObject} is comparable, matches the given type and passes through a set of filters.
     * @param o The object to test.
     * @param type The type of comparable {@link ArenaObject}.
     * @param filters The set of filters.
     * @return Whether the object satisfies all requirements.
     */
    default boolean isComparableAndAllSatisfied(ArenaObject o, StoredComparableType type,
            List<ArenaObjectSortedSelector<T>> filters) {

        if (!(type.getObjectClass().isAssignableFrom(o.getClass()))) return false;

        for (ArenaObjectSelector s : filters) {
            if (!s.isInSelection(o)) return false;
        }

        return true;
    }

    /**
     * Sorts the result of the query.
     * @param result The result to sort.
     * @param option The sorting option.
     */
    default void sortResult(List<T> result, SortOption option) {
        switch (option) {
            case ASCENDING: result.sort(null); return;
            case DESCENDING: result.sort((o1, o2) -> o2.compareTo(o1)); return;
        }
    }
}