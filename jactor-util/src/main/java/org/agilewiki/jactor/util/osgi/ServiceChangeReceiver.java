package org.agilewiki.jactor.util.osgi;

import org.agilewiki.jactor.api.Actor;
import org.agilewiki.jactor.api.Transport;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;

import java.util.Map;

public interface ServiceChangeReceiver<T> extends Actor {
    public void serviceChange(final ServiceEvent _event,
                              final Map<ServiceReference, T> _tracked,
                              final Transport _transport) throws Exception;
}
