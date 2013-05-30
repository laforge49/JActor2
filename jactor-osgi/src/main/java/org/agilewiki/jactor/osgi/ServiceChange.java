package org.agilewiki.jactor.osgi;

import org.agilewiki.jactor.api.Transport;
import org.agilewiki.jactor.api.UnboundRequestBase;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;

import java.util.Map;
import java.util.Objects;

/**
 * Request used to pass the changed list of services to the listener.
 *
 * @param <T> The expected service type.
 */
public class ServiceChange<T> extends
        UnboundRequestBase<Void, ServiceChangeReceiver> {
    private final ServiceEvent event;
    private final Map<ServiceReference, T> tracked;

    /**
     * Creates a ServiceChange request
     */
    public ServiceChange(final ServiceEvent _event,
                         final Map<ServiceReference, T> _tracked) {
        event = _event;
        tracked = Objects.requireNonNull(_tracked, "_tracked");
    }

    /**
     * Process the request, by calling serviceChange() on the listener.
     */
    @Override
    public void processRequest(final ServiceChangeReceiver _targetActor,
                               final Transport<Void> _transport) throws Exception {
        _targetActor.serviceChange(event, tracked, _transport);
    }
}
