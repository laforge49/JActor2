package org.agilewiki.jactor.util.osgi.durable;

import org.agilewiki.jactor.api.Properties;
import org.agilewiki.jactor.api.Transport;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.TreeMap;

/**
 * An activator that provides both a mailbox factory and a registered factory locator service.
 */
abstract public class FactoryLocatorActivator extends FactoryLocatorActivator0 {
    private final Logger log = LoggerFactory.getLogger(FactoryLocatorActivator.class);

    @Override
    public void start(final BundleContext _bundleContext) throws Exception {
        initializeActivator(_bundleContext);
        mailboxFactoryStart();
        createFactoryLocator();
        beginReq().signal();
    }

    protected boolean configImports() {
        return true;
    }

    @Override
    protected void begin(final Transport<Void> _transport) throws Exception {
        if (!configImports())
            factoryLocator.register(bundleContext);
        managedServiceRegistration();
        _transport.processResponse(null);
    }

    protected void createFactoryLocator() throws Exception {
        super.createFactoryLocator();
        Properties properties = getMailboxFactory().getProperties();
        properties.putProperty("factoryLocator", factoryLocator);
    }

    @Override
    protected void configInitialized() {
        if (configImports()) {
            Dictionary<String, ?> config = getConfig();
            Enumeration<String> keys = config.keys();
            TreeMap<String, String> imports = new TreeMap<String, String>();
            while (keys.hasMoreElements()) {
                String key = keys.nextElement();
                if (!(key.startsWith("import_")))
                    continue;
                String value = (String) config.get(key);
                imports.put(key, value);
            }
            Iterator<String> it = imports.values().iterator();
            while(it.hasNext()) {
                String value = it.next();
                int i = value.indexOf('|');
                if (i > -1) {
                    String bundleName = value.substring(0, i);
                    String niceVersion = value.substring(i + 1);
                    log.info("******************* "+bundleName+" | "+niceVersion);
                } else {
                    //todo
                }
            }
            //todo
            factoryLocator.register(bundleContext);
        }
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        setClosing();
        getMailboxFactory().close();
    }
}
