package project.event;

import java.util.Iterator;
import java.util.LinkedList;

import project.event.eventargs.EventArgs;

/**
 * A class that manages an event.
 * @param <TEventArgs> A class type denoting the set of arguments for the event.
 */
public class EventManager<TEventArgs extends EventArgs> {

    /**
     * The objects which are subscribed to the event.
     */
    protected LinkedList<EventHandler<TEventArgs>> subscribers = new LinkedList<>();

    /**
     * Whether the event manager is currently invoking an event.
     */
    private boolean isInvokingEvent = false;

    /**
     * The list of objects that are to be subscribed to the event at the end of the invocation.
     */
    private LinkedList<EventHandler<TEventArgs>> toSubscribe = new LinkedList<>();

    /**
     * The list of objects that are to be unsubscribed from the event at the end of the invocation.
     */
    private LinkedList<EventHandler<TEventArgs>> toUnsubscribe = new LinkedList<>();

    /**
     * Subscribes an event handler to the event.
     * Does nothing if the handler is already subscribed to the event.
     * @param handler The event handler.
     * @return <code>true</code> iff the handler was originally not subscribed to the event.
     */
    public boolean subscribe(EventHandler<TEventArgs> handler) {
        if (!subscribers.contains(handler) && !toSubscribe.contains(handler)) {
            if (isInvokingEvent) toSubscribe.add(handler);
            else subscribers.add(handler);
            return true;
        }

        return false;
    }
 
    /**
     * Unsubscribes an event handler to the event.
     * Does nothing if the handler is not already subscribed to the event.
     * @param handler The event handler.
     * @return <code>true</code> iff the handler was originally subscribed to the event.
     */
    public boolean unsubscribe(EventHandler<TEventArgs> handler) {
        if (subscribers.contains(handler) && !toUnsubscribe.contains(handler)) {
            if (isInvokingEvent) toUnsubscribe.add(handler);
            else subscribers.remove(handler);

            return true;
        }

        return false;
    }

    /**
     * Invokes the event on each subscriber.
     * @param sender The sender of the event.
     * @param args The arguments of the event.
     */
    public void invoke(Object sender, TEventArgs args) {
        isInvokingEvent = true;

        Iterator<EventHandler<TEventArgs>> iterator = subscribers.iterator();
        while (iterator.hasNext()) {
            iterator.next().handleEvent(sender, args);
        }

        isInvokingEvent = false;
        
        while (!toSubscribe.isEmpty()) subscribers.add(toSubscribe.poll());
        while (!toUnsubscribe.isEmpty()) subscribers.remove(toUnsubscribe.poll());
    }
}