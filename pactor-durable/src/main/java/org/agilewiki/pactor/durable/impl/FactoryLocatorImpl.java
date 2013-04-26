/*
 * Copyright 2011 Bill La Forge
 *
 * This file is part of AgileWiki and is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License (LGPL) as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 * or navigate to the following url http://www.gnu.org/licenses/lgpl-2.1.txt
 *
 * Note however that only Scala, Java and JavaScript files are being covered by LGPL.
 * All other files are covered by the Common Public License (CPL).
 * A copy of this license is also included and can be
 * found as well at http://www.opensource.org/licenses/cpl1.0.txt
 */
package org.agilewiki.pactor.durable.impl;

import org.agilewiki.pactor.Mailbox;
import org.agilewiki.pactor.MailboxFactory;
import org.agilewiki.pactor.durable.Factory;
import org.agilewiki.pactor.durable.FactoryLocator;
import org.agilewiki.pactor.durable.PASerializable;
import org.agilewiki.pautil.Ancestor;
import org.agilewiki.pautil.AncestorBase;
import org.osgi.framework.Bundle;

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

    /**
     * A table which maps type names to actor factories.
     */
    private ConcurrentSkipListMap<String, Factory> types = new ConcurrentSkipListMap();

    public void configure(final String _bundleName) {
        bundleName = _bundleName;
    }

    public void configure(final String _bundleName, final String _version, final String _location) {
        bundleName = _bundleName;
        version  = _version;
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
    public PASerializable newSerializable(String jidType, Mailbox mailbox, Ancestor parent)
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
    public Factory getFactory(String jidType)
            throws Exception {
        Factory af = _getFactory(jidType);
        if (af == null) {
            throw new IllegalArgumentException("Unknown jid type: " + jidType);
        }
        return af;
    }

    @Override
    public Factory _getFactory(String actorType)
            throws Exception {
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
                af = it.next()._getFactory(actorType);
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
    @Override
    public void registerFactory(Factory factory)
            throws Exception {
        String actorType = factory.getName();
        String factoryKey = actorType + "|" + bundleName + "|" + version;
        Factory old = types.get(factoryKey);
        ((FactoryImpl) factory).configure(this);
        if (old == null) {
            types.put(factoryKey, factory);
        } else if (!old.equals(factory))
            throw new IllegalArgumentException("IncDesImpl type is already defined: " + actorType);
    }
}
