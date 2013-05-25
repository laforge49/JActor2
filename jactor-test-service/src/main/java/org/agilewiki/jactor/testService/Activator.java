package org.agilewiki.jactor.testService;

import org.agilewiki.jactor.api.*;
import org.agilewiki.jactor.testIface.Hello;
import org.agilewiki.jactor.util.durable.Durables;
import org.agilewiki.jactor.util.osgi.MailboxFactoryActivator;
import org.agilewiki.jactor.util.osgi.durable.FactoriesImporter;
import org.agilewiki.jactor.util.osgi.durable.FactoryLocatorActivator;
import org.osgi.framework.*;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Hashtable;

public class Activator extends FactoryLocatorActivator {
    private final Logger logger = LoggerFactory.getLogger(Activator.class);

    @Override
    protected void begin(final Transport<Void> _transport) throws Exception {
        FactoriesImporter factoriesImporter = new FactoriesImporter(getMailbox());
        String fs = "(&" +
                "(objectClass=org.agilewiki.jactor.util.durable.FactoryLocator)" +
                "(&(bundleName=jactor-util)(bundleVersion=0.0.1-SNAPSHOT))" +
                ")";
        Filter filter = bundleContext.createFilter(fs);
        factoriesImporter.startReq(filter).send(getMailbox(), new ResponseProcessor<Void>() {
            @Override
            public void processResponse(Void response) throws Exception {
                factoryLocator.register(bundleContext);
                factoryLocator.setEssentialService(getMailboxFactory());
                HelloService hello = new HelloService(bundleContext, getMailbox());
                ServiceRegistration hsr = bundleContext.registerService(
                        Hello.class.getName(),
                        hello,
                        new Hashtable<String, String>());
                Version version = bundleContext.getBundle().getVersion();

                Hashtable<String, String> mp = new Hashtable<String, String>();
                mp.put(Constants.SERVICE_PID, "org.agilewiki.jactor.testService." + version.toString());
                ServiceRegistration msr = bundleContext.registerService(
                        ManagedService.class.getName(),
                        hello,
                        mp);


                _transport.processResponse(null);
            }
        });
    }
}
