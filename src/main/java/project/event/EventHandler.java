package project.event;

import project.event.eventargs.EventArgs;

/**
 * Functional interface to handle an event.
 * @param <TEventArgs> A class type denoting the set of arguments.
 */
@FunctionalInterface
public interface EventHandler<TEventArgs extends EventArgs> {

    /**
     * Handles an event.
     * @param sender The sender of the event.
     * @param args The arguments of the event.
     */
    public abstract void handleEvent(Object sender, TEventArgs args);
}