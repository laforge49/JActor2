package org.agilewiki.jactor.util.osgi;

import org.agilewiki.jactor.api.MailboxFactory;
import org.agilewiki.jactor.api.Properties;
import org.agilewiki.jactor.impl.DefaultMailboxFactoryImpl;
import org.agilewiki.jactor.util.JAProperties;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;

/**
 * A basic activator with a MailboxFactory,
 * with a reference to the BundleContext stored in the bundleContext property
 * in the MailboxFactory.
 */
public class MailboxFactoryActivator implements BundleActivator, AutoCloseable {

    /**
     * Returns the BundleContext saved in the bundleContext property of a MailboxFactory.
     *
     * @param _mailboxFactory    The mailbox factory.
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
    public static String niceVersion(Version version) {
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

    /**
     * The mailbox factory used by the bundle.
     */
    private MailboxFactory mailboxFactory;

    /**
     * The properties held by the mailbox factory.
     */
    private JAProperties jaProperties;

    /**
     * The bundle context.
     */
    protected BundleContext bundleContext;

    /**
     * True when the bundle is closed or closing.
     */
    private boolean closing;

    @Override
    public void start(final BundleContext _bundleContext) throws Exception {
        setBundleContext(_bundleContext);
        mailboxFactoryStart();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        setClosing();
        mailboxFactory.close();
    }

    /**
     * Save the bundle context.
     * @param _bundleContext    The bundle context.
     */
    protected final void setBundleContext(final BundleContext _bundleContext) {
        bundleContext = _bundleContext;
    }

    /**
     * Returns the mailbox factory used by the bundle.
     * @return The mailbox factory.
     */
    protected MailboxFactory getMailboxFactory() {
        return mailboxFactory;
    }

    /**
     * Create and initialize the mailbox factory.
     * The Properties object of the mailbox factory is created
     * and a bundleContext is added to it.
     * This activator is also added to the close set of the mailbox factory.
     */
    protected final void mailboxFactoryStart() throws Exception {
        mailboxFactory = new DefaultMailboxFactoryImpl();
        mailboxFactory.addAutoClosable(this);
        jaProperties = new JAProperties(mailboxFactory, null);
        jaProperties.putProperty("bundleContext", bundleContext);
    }

    /**
     * Returns true when closing or closed.
     *
     * @return True when closing or closed.
     */
    protected final boolean isBundleClosing() {
        return closing;
    }

    /**
     * Mark as closing.
     */
    protected final void setClosing() {
        closing = true;
    }

    /**
     * Stop the bundle unless already closing.
     */
    @Override
    public void close() throws Exception {
        if (closing)
            return;
        Bundle bundle = bundleContext.getBundle();
        bundle.stop(Bundle.STOP_TRANSIENT);
    }
}
