package org.agilewiki.jactor2.utilImpl.durable;

import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.util.Ancestor;
import org.agilewiki.jactor2.util.AncestorBase;
import org.agilewiki.jactor2.util.durable.Factory;
import org.agilewiki.jactor2.util.durable.FactoryLocator;
import org.agilewiki.jactor2.util.durable.FactoryLocatorClosedException;
import org.agilewiki.jactor2.util.durable.JASerializable;

import java.util.Iterator;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * An actor for defining jid types and creating instances.
 */
public class FactoryLocatorImpl extends AncestorBase implements FactoryLocator,
        AutoCloseable {

    private final CopyOnWriteArrayList<FactoryLocator> factoryImports = new CopyOnWriteArrayList();
    private String bundleName = "";
    private String niceVersion = "";
    private String location = "";
    private String locatorKey;
    private String descriptor;
    private boolean closed;

    /**
     * A table which maps type names to actor factories.
     */
    private final ConcurrentSkipListMap<String, Factory> types = new ConcurrentSkipListMap();

    public void configure(final String _bundleName, final String _niceVersion,
            final String _location) {
        bundleName = _bundleName;
        niceVersion = _niceVersion;
        location = _location;
    }

    public String getBundleName() {
        return bundleName;
    }

    public String getNiceVersion() {
        return niceVersion;
    }

    public String getLocation() {
        return location;
    }

    public String getLocatorKey() {
        if (locatorKey == null) {
            locatorKey = bundleName + "|" + getNiceVersion();
        }
        return locatorKey;
    }

    public void importFactoryLocator(final FactoryLocator _factoryLocator) {
        factoryImports.add(_factoryLocator);
    }

    /**
     * Creates a new actor.
     *
     * @param jidType The jid type.
     * @param reactor A processing which may be shared with other actors, or null.
     * @param parent  The parent actor to which unrecognized requests are forwarded, or null.
     * @return The new jid.
     */
    public JASerializable newSerializable(final String jidType,
            final Reactor reactor, final Ancestor parent) throws Exception {
        if (reactor == null) {
            throw new IllegalArgumentException("processing may not be null");
        }
        final Factory af = getFactory(jidType);
        return af.newSerializable(reactor, parent);
    }

    /**
     * Returns the requested actor factory.
     *
     * @param jidType The jid type.
     * @return The registered actor factory.
     */
    @Override
    public Factory getFactory(final String jidType) throws Exception {
        final Factory af = _getFactory(jidType);
        if (af == null) {
            throw new IllegalArgumentException("Unknown jid type: " + jidType);
        }
        return af;
    }

    public Factory _getFactory(final String actorType) throws Exception {
        if (closed) {
            throw new FactoryLocatorClosedException();
        }
        String factoryKey = null;
        if (actorType.contains("|")) {
            factoryKey = actorType;
        } else {
            factoryKey = actorType + "|" + bundleName + "|" + niceVersion;
        }
        Factory af = types.get(factoryKey);
        if (af == null) {
            final Iterator<FactoryLocator> it = factoryImports.iterator();
            while (it.hasNext()) {
                try {
                    af = ((FactoryLocatorImpl) it.next())
                            ._getFactory(actorType);
                    if (af != null) {
                        return af;
                    }
                } catch (final FactoryLocatorClosedException flce) {
                    close();
                    throw flce;
                }
            }
        }
        return af;
    }

    /**
     * Register an actor factory.
     *
     * @param factory An actor factory.
     */
    public void registerFactory(final Factory factory)
            throws FactoryLocatorClosedException {
        if (closed) {
            throw new FactoryLocatorClosedException();
        }
        final String actorType = factory.getName();
        final String factoryKey = actorType + "|" + bundleName + "|"
                + niceVersion;
        final Factory old = types.get(factoryKey);
        ((FactoryImpl) factory).configure(factoryKey);
        if (old == null) {
            types.put(factoryKey, factory);
        } else if (!old.equals(factory)) {
            throw new IllegalArgumentException(
                    "IncDesImpl type is already defined differently: "
                            + old.getFactoryKey());
        }
    }

    @Override
    public void close() throws Exception {
        closed = true;
    }
}
