package org.agilewiki.jactor.util.osgi;

import org.agilewiki.jactor.api.*;
import org.agilewiki.jactor.util.durable.Durables;
import org.agilewiki.jactor.util.durable.incDes.Root;
import org.agilewiki.jactor.util.osgi.durable.OsgiFactoryLocator;
import org.agilewiki.jactor.util.osgi.serviceTracker.LocateService;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.Version;

final public class Osgi {

    /**
     * Returns the BundleContext saved in the bundleContext property of a MailboxFactory.
     *
     * @param _mailboxFactory The mailbox factory.
     * @return The BundleContext.
     */
    public static BundleContext getBundleContext(final MailboxFactory _mailboxFactory) {
        Properties p = _mailboxFactory.getProperties();
        return (BundleContext) p.getProperty("bundleContext");
    }

    /**
     * Returns the version in the form major.minor.micro or major.minor.micro-qualifier.
     * This is in contrast to Version.toString, which uses a . rather than a - with a qualifier.
     *
     * @param version The version.
     * @return The formatted version.
     */
    public static String getNiceVersion(Version version) {
        int q = version.getQualifier().length();
        StringBuffer result = new StringBuffer(20 + q);
        result.append(version.getMajor());
        result.append(".");
        result.append(version.getMinor());
        result.append(".");
        result.append(version.getMicro());
        if (q > 0) {
            result.append("-");
            result.append(version.getQualifier());
        }
        return result.toString();
    }

    public static OsgiFactoryLocator getOsgiFactoryLocator(final Mailbox _mailbox) {
        return (OsgiFactoryLocator) Durables.getFactoryLocator(_mailbox);
    }

    public static OsgiFactoryLocator getOsgiFactoryLocator(final MailboxFactory _mailboxFactory) {
        return (OsgiFactoryLocator) Durables.getFactoryLocator(_mailboxFactory);
    }

    public static Filter factoryLocatorFilter(final BundleContext _bundleContext,
                                              final String _bundleName,
                                              final String _niceVersion) throws Exception {
        return _bundleContext.createFilter("(&" +
                "(objectClass=org.agilewiki.jactor.util.osgi.durable.OsgiFactoryLocator)" +
                "(&(bundleName=" + _bundleName + ")(bundleVersion=" + _niceVersion + "))" +
                ")");
    }

    public static Request<Root> contextCopyReq(final Root _root) throws Exception {
        return new RequestBase<Root>(_root.getMailbox()) {
            @Override
            public void processRequest(final Transport<Root> _transport) throws Exception {
                String location = _root.getBundleLocation();
                BundleContext bundleContext = getBundleContext(_root.getMailbox().getMailboxFactory());
                Bundle bundle = bundleContext.installBundle(location);
                bundle.start();
                Version version = bundle.getVersion();
                LocateService<OsgiFactoryLocator> locateService = new LocateService<OsgiFactoryLocator>(
                        _root.getMailbox(), OsgiFactoryLocator.class.getName());
                locateService.getReq().send(_root.getMailbox(), new ResponseProcessor<OsgiFactoryLocator>() {
                    @Override
                    public void processResponse(OsgiFactoryLocator response) throws Exception {
                        Mailbox newMailbox = response.getMailboxFactory().createMailbox();
                        _root.copyReq(newMailbox).send(_root.getMailbox(), (Transport) _transport);
                    }
                });
            }
        };
    }
}
