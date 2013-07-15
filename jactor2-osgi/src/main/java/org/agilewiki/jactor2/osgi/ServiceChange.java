package org.agilewiki.jactor2.osgi;

import org.agilewiki.jactor2.api.Transport;
import org.agilewiki.jactor2.api.RequestBase;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;

import java.util.Map;
import java.util.Objects;

/**
 * Unbound boundRequest used to pass the changed list of services to the listener.
 *
 * @param <T> The expected service type.
 */
public class ServiceChange<T> extends
        RequestBase<Void, ServiceChangeReceiver> {

    /**
     * The service event to be passed.
     */
    private final ServiceEvent event;

    /**
     * The tracked services to be passed.
     */
    private final Map<ServiceReference, T> tracked;

    /**
     * Creates a ServiceChange boundRequest.
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
    public void processRequest(final ServiceChangeReceiver _targetActor,
                               final Transport<Void> _transport) throws Exception {
        _targetActor.serviceChange(event, tracked, _transport);
    }
}
