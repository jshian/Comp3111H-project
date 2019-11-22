package project.query;

import java.util.Iterator;
import java.util.LinkedList;

import org.checkerframework.checker.nullness.qual.Nullable;

abstract class BinaryNode<T> {

    private T data;

    private @Nullable BinaryNode<?> left;
    private @Nullable BinaryNode<?> right;

    /**
     * Constructs a newly allocated ArenaObjectSelectorNode object.
     * @param data The data to store in the node.
     */
    BinaryNode(T data) {
        this.data = data;
    }

    /**
     * Returns the data stored in this node.
     * @return The data stored in this node.
     */
    T getData() { return data; }

    /**
     * Returns whether this node is a leaf node of the tree.
     * @return Whether this node is a leaf node of the tree.
     */
    boolean isLeaf() { return (left == null && right == null); }

    /**
     * Returns the left child of the node.
     * @return The left child of the node, or <code>null</code> if it does not exist.
     */
    @Nullable BinaryNode<?> getLeft() { return left; }

    /**
     * Returns the right child of the node.
     * @return The right child of the node, or <code>null</code> if it does not exist.
     */
    @Nullable BinaryNode<?> getRight() { return right; }
}