package org.agilewiki.jactor.util.osgi;

import org.agilewiki.jactor.api.Transport;
import org.agilewiki.jactor.api.UnboundRequestBase;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;

import java.util.Map;

public class ServiceChange<T> extends UnboundRequestBase<Void, ServiceChangeReceiver> {
    private final ServiceEvent event;
    private final Map<ServiceReference, T> tracked;

    public ServiceChange(final ServiceEvent _event, final Map<ServiceReference, T> _tracked) {
        event = _event;
        tracked = _tracked;
    }

    @Override
    public void processRequest(ServiceChangeReceiver _targetActor, Transport<Void> _transport) throws Exception {
        _targetActor.serviceChange(event, tracked, _transport);
    }
}
