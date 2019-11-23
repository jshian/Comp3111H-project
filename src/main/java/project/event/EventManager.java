package project.event;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

import project.event.eventargs.EventArgs;
import project.event.eventsets.EventSet;

/**
 * A class that manages a certain set of events with a certain set of arguments.
 * @param <TEvent> An enum type denoting the set of events.
 * @param <TEventArgs> A class type denoting the set of arguments.
 */
public class EventManager<TEvent extends Enum<TEvent> & EventSet, TEventArgs extends EventArgs> {

    HashMap<TEvent, LinkedList<EventHandler<TEventArgs>>> subscribers = new HashMap<>();

    /**
     * Subscribes an event handler to the specified event.
     * Does nothing if the handler is already subscribed to the event.
     * @param event The event.
     * @param handler The event handler.
     * @return <code>true</code> iff the handler was originally not subscribed to the event.
     */
    public boolean subscribe(TEvent event, EventHandler<TEventArgs> handler) {
        if (subscribers.keySet().contains(event)) {
            LinkedList<EventHandler<TEventArgs>> list = subscribers.get(event);
            if (!list.contains(handler)) {
                list.add(handler);
                return true;
            }
        } else {
            subscribers.put(event, new LinkedList<EventHandler<TEventArgs>>(Arrays.asList(handler)));
            return true;
        }

        return false;
    }
 
    /**
     * Unsubscribes an event handler to the specified event.
     * Does nothing if the handler is not already subscribed to the event.
     * @param event The event.
     * @param handler The event handler.
     * @return <code>true</code> iff the handler was originally subscribed to the event.
     */
    public boolean unsubscribe(TEvent event, EventHandler<TEventArgs> handler) {
        if (subscribers.keySet().contains(event)) {
            return subscribers.get(event).remove(handler);
        }

        return false;
    }

    /**
     * Invokes the event on each subscriber.
     * @param event The event.
     * @param sender The sender of the event.
     * @param args The arguments of the event.
     */
    public void invoke(TEvent event, Object sender, TEventArgs args) {
        if (subscribers.keySet().contains(event)) {
            for (EventHandler<TEventArgs> s : subscribers.get(event)) {
                s.handleEvent(sender, args);
            }
        }
    }
}