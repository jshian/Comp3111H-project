package project.event;

import java.util.LinkedList;

import project.event.eventargs.EventArgs;

/**
 * A class that manages a certain set of events with a certain set of arguments.
 * @param <TEvent> An enum type denoting the set of events.
 * @param <TEventArgs> A class type denoting the set of arguments.
 */
public class EventManager<TEventArgs extends EventArgs> {

    protected LinkedList<EventHandler<TEventArgs>> subscribers = new LinkedList<>();

    /**
     * Subscribes an event handler to the specified event.
     * Does nothing if the handler is already subscribed to the event.
     * @param handler The event handler.
     * @return <code>true</code> iff the handler was originally not subscribed to the event.
     */
    public boolean subscribe(EventHandler<TEventArgs> handler) {
        if (!subscribers.contains(handler)) {
            subscribers.add(handler);
            return true;
        }

        return false;
    }
 
    /**
     * Unsubscribes an event handler to the specified event.
     * Does nothing if the handler is not already subscribed to the event.
     * @param handler The event handler.
     * @return <code>true</code> iff the handler was originally subscribed to the event.
     */
    public boolean unsubscribe(EventHandler<TEventArgs> handler) {
        return subscribers.remove(handler);
    }

    /**
     * Invokes the event on each subscriber.
     * @param sender The sender of the event.
     * @param args The arguments of the event.
     */
    public void invoke(Object sender, TEventArgs args) {
        for (EventHandler<TEventArgs> s : subscribers) {
            s.handleEvent(sender, args);
        }
    }
}