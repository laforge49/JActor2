package org.agilewiki.jactor2.testService;

import org.agilewiki.jactor2.osgi.FactoryLocatorActivator;
import org.agilewiki.jactor2.testIface.Hello;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Dictionary;
import java.util.Hashtable;

public class Activator extends FactoryLocatorActivator {
    private final Logger logger = LoggerFactory.getLogger(Activator.class);
    private HelloService hello;

    @Override
    public void updated(final Dictionary<String, ?> _config) throws ConfigurationException {
        hello.updated(_config);
        super.updated(_config);
    }

    @Override
    protected void configInitialized() throws ConfigurationException {
        super.configInitialized();
        ServiceRegistration hsr = bundleContext.registerService(
                Hello.class.getName(),
                hello,
                new Hashtable<String, String>());
    }

    @Override
    protected void process() throws Exception {
        hello = new HelloService(bundleContext, getMailbox());
        managedServiceRegistration();
    }
}
