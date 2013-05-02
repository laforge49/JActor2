package org.agilewiki.jactor.utilImpl.durable;

import org.agilewiki.jactor.api.Mailbox;
import org.agilewiki.jactor.util.Ancestor;
import org.agilewiki.jactor.util.AncestorBase;
import org.agilewiki.jactor.util.durable.Factory;
import org.agilewiki.jactor.util.durable.FactoryLocator;
import org.agilewiki.jactor.util.durable.JASerializable;

import java.util.Iterator;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * An actor for defining jid types and creating instances.
 */
public class FactoryLocatorImpl extends AncestorBase implements FactoryLocator {

    private ConcurrentSkipListSet<FactoryLocator> factoryImports = new ConcurrentSkipListSet();
    private String bundleName = "";
    private String version = "";
    private String location = "";
    private String locatorKey;
    private String descriptor;

    /**
     * A table which maps type names to actor factories.
     */
    private ConcurrentSkipListMap<String, Factory> types = new ConcurrentSkipListMap();

    public void configure(final String _bundleName, final String _version, final String _location) {
        bundleName = _bundleName;
        version = _version;
        location = _location;
    }

    public String getBundleName() {
        return bundleName;
    }

    public String getVersion() {
        return version;
    }

    public String getLocation() {
        return location;
    }

    public String getDescriptor() {
        if (descriptor == null)
            descriptor = getLocatorKey() + "|" + location;
        return descriptor;
    }

    public String getLocatorKey() {
        if (locatorKey == null)
            locatorKey = bundleName + "|" + getVersion();
        return locatorKey;
    }

    public void importFactories(final FactoryLocator _factoryLocator) {
        factoryImports.add(_factoryLocator);
    }

    /**
     * Creates a new actor.
     *
     * @param jidType The jid type.
     * @param mailbox A mailbox which may be shared with other actors, or null.
     * @param parent  The parent actor to which unrecognized requests are forwarded, or null.
     * @return The new jid.
     */
    public JASerializable newSerializable(String jidType, Mailbox mailbox, Ancestor parent)
            throws Exception {
        if (mailbox == null)
            throw new IllegalArgumentException("mailbox may not be null");
        Factory af = getFactory(jidType);
        return af.newSerializable(mailbox, parent);
    }

    /**
     * Returns the requested actor factory.
     *
     * @param jidType The jid type.
     * @return The registered actor factory.
     */
    @Override
    public Factory getFactory(String jidType) {
        Factory af = _getFactory(jidType);
        if (af == null) {
            throw new IllegalArgumentException("Unknown jid type: " + jidType);
        }
        return af;
    }

    public Factory _getFactory(String actorType) {
        String factoryKey = null;
        if (actorType.contains("|")) {
            factoryKey = actorType;
        } else {
            factoryKey = actorType + "|" + bundleName + "|" + version;
        }
        Factory af = types.get(factoryKey);
        if (af == null) {
            Iterator<FactoryLocator> it = factoryImports.iterator();
            while (it.hasNext()) {
                af = ((FactoryLocatorImpl) it.next())._getFactory(actorType);
                if (af != null)
                    return af;
            }
        }
        return af;
    }

    /**
     * Register an actor factory.
     *
     * @param factory An actor factory.
     */
    public void registerFactory(Factory factory) {
        String actorType = factory.getName();
        String factoryKey = actorType + "|" + bundleName + "|" + version;
        Factory old = types.get(factoryKey);
        ((FactoryImpl) factory).configure(factoryKey);
        if (old == null) {
            types.put(factoryKey, factory);
        } else if (!old.equals(factory))
            throw new IllegalArgumentException("IncDesImpl type is already defined differently: " + old.getFactoryKey());
    }
}
