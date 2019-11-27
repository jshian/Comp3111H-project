package project.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Helper class for comparing collections.
 */
public class CollectionComparator {

    /**
     * Returns whether the set of elements of the two collections is equivalent.
     * @param <T> The type of the elements.
     * @param l1 The first collection.
     * @param l2 The second collection.
     * @return Whether the set of elements of the two collections is equivalent.
     */
    public static <T> boolean isElementSetEqual(Collection<T> l1, Collection<T> l2) {
        return new HashSet<T>(l1).equals(new HashSet<T>(l2));
    }

    /**
     * Returns whether the set of elements of the two queues is equivalent and sorted in the same order.
     * @param <T> The type of the elements.
     * @param l1 The first collection.
     * @param l2 The second collection.
     * @return Whether the set of elements of the two queues is equivalent and sorted in the same order.
     */
    public static <T> boolean isElementSetAndOrderEqual(Queue<T> q1, Queue<T> q2) {
        LinkedList<T> q1_cpy = new LinkedList<>(q1);
        LinkedList<T> q2_cpy = new LinkedList<>(q2);

        while (!q1_cpy.isEmpty()) {
            T e1 = q1_cpy.poll();
            if (q2_cpy.poll() != e1) return false;
        }

        return q2_cpy.isEmpty();
    }

}