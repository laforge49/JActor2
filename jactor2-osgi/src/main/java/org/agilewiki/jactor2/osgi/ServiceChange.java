package org.agilewiki.jactor2.osgi;

import org.agilewiki.jactor2.core.messaging.Event;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;

import java.util.Map;
import java.util.Objects;

/**
 * Unbound request used to pass the changed list of services to the listener.
 *
 * @param <T> The expected service type.
 */
public class ServiceChange<T> extends
        Event<ServiceChangeReceiver> {

    /**
     * The service event to be passed.
     */
    private final ServiceEvent event;

    /**
     * The tracked services to be passed.
     */
    private final Map<ServiceReference, T> tracked;

    /**
     * Creates a ServiceChange request.
     *
     * @param _event   The service event to be passed.
     * @param _tracked The tracked services to be passed.
     */
    public ServiceChange(final ServiceEvent _event,
                         final Map<ServiceReference, T> _tracked) {
        event = _event;
        tracked = Objects.requireNonNull(_tracked, "_tracked");
    }

    @Override
    public void processEvent(final ServiceChangeReceiver _targetBlade) throws Exception {
        _targetBlade.serviceChange(event, tracked);
    }
}
