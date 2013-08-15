package org.agilewiki.jactor2.testService;

import org.agilewiki.jactor2.core.ActorBase;
import org.agilewiki.jactor2.core.processing.Mailbox;
import org.agilewiki.jactor2.testIface.Hello;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Dictionary;

public class HelloService extends ActorBase implements Hello, ManagedService {
    private final Logger logger = LoggerFactory.getLogger(HelloService.class);
    private BundleContext context;
    private Dictionary<String, ?> properties;

    public HelloService(final BundleContext _context, Mailbox mailbox) throws Exception {
        initialize(mailbox);
        context = _context;
    }

    @Override
    public String getMessage() throws Exception {
        if (properties == null)
            return "Hello Pax!";
        return (String) properties.get("msg");
    }

    @Override
    public void updated(final Dictionary<String, ?> _properties) throws ConfigurationException {
        properties = _properties;
    }
}
