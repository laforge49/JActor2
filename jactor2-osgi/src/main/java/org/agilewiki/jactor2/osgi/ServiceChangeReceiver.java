package org.agilewiki.jactor2.osgi;

import org.agilewiki.jactor2.api.Actor;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;

import java.util.Map;

/**
 * "Listener" interface that must be implemented by an actor that wants to use
 * a JAServiceTracker to keep track of services.
 * <p/>
 * NOTE: This interface implies that one actor can only track services of one
 * type (because of the generic type T) of other actor. I'm pretty sure there
 * are many cases where one actor want to keep track of multiple actor types,
 * by using multiple JAServiceTracker. Maybe this is not so optimal ...
 *
 * @param <T> The type of the service expected.
 */
public interface ServiceChangeReceiver<T> extends Actor {

    /**
     * Called from within the actor's own mailbox, using a request, when
     * registering with a JAServiceTracker, or when some service goes up or
     * down.
     *
     * @param _event   The service event, will be null in the initial service change.
     * @param _tracked The available services.
     */
    public void serviceChange(final ServiceEvent _event,
                              final Map<ServiceReference, T> _tracked)
            throws Exception;
}
