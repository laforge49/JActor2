package org.agilewiki.jactor2.osgi;

import org.agilewiki.jactor2.core.ActorBase;
import org.agilewiki.jactor2.core.context.JAContext;
import org.agilewiki.jactor2.core.mailbox.AtomicMailbox;
import org.agilewiki.jactor2.core.messaging.Event;
import org.agilewiki.jactor2.util.JAProperties;
import org.osgi.framework.*;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

import java.util.Dictionary;
import java.util.Hashtable;

/**
 * A basic activator with a JAContext,
 * with a reference to the BundleContext stored in the bundleContext property
 * in the JAContext.
 */
abstract public class JAContextActivator
        extends ActorBase implements BundleActivator, ManagedService, AutoCloseable {

    /**
     * The version of the bundle.
     */
    private Version version;

    /**
     * Config properties, or null.
     */
    private Dictionary<String, ?> config;

    /**
     * The mailbox factory used by the bundle.
     */
    private JAContext jaContext;

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
        initializeActivator(_bundleContext);
        jaContextStart();
        begin();
    }

    /**
     * Begins the activator's asynchronous processing.
     *
     * @return The request.
     */
    protected void begin() throws Exception {
        new Event<JAContextActivator>() {
            @Override
            public void processEvent(JAContextActivator _targetActor) throws Exception {
                process();
            }
        }.signal(this);
    }

    /**
     * The activator's asynchronous processing.
     */
    protected void process() throws Exception {
        managedServiceRegistration();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        setClosing();
        jaContext.close();
    }

    /**
     * Initialize the activator.
     *
     * @param _bundleContext The bundle context.
     */
    protected final void initializeActivator(final BundleContext _bundleContext) {
        bundleContext = _bundleContext;
        version = bundleContext.getBundle().getVersion();
    }

    /**
     * Returns the mailbox factory used by the bundle.
     *
     * @return The mailbox factory.
     */
    protected JAContext getJAContext() {
        return jaContext;
    }

    /**
     * Create and initialize the mailbox factory.
     * The Properties object of the mailbox factory is created
     * and a bundleContext is added to it.
     * The activator is also added to the close set of the mailbox factory
     * and the activator is given a mailbox that may block.
     */
    protected final void jaContextStart() throws Exception {
        jaContext = new JAContext();
        jaContext.addAutoClosable(this);
        jaProperties = new JAProperties(jaContext, null);
        jaProperties.putProperty("bundleContext", bundleContext);
        initialize(new AtomicMailbox(jaContext));
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

    /**
     * Returns the bundle version.
     *
     * @return The bundle version.
     */
    protected Version getVersion() {
        return version;
    }

    /**
     * Returns the bundle version in the form 1.2.3 or 1.2.3-SNAPSHOT.
     *
     * @return The nice form of the version.
     */
    protected String getNiceVersion() {
        return Osgi.getNiceVersion(getVersion());
    }

    /**
     * Register the activator as a managed service.
     */
    protected void managedServiceRegistration() {
        Hashtable<String, String> mp = new Hashtable<String, String>();
        mp.put(Constants.SERVICE_PID, this.getClass().getName() + "." + getVersion().toString());
        ServiceRegistration msr = bundleContext.registerService(
                ManagedService.class.getName(),
                this,
                mp);
    }

    @Override
    public void updated(final Dictionary<String, ?> _config) throws ConfigurationException {
        config = _config;
    }

    /**
     * Returns the contents of the config file.
     *
     * @return The contents of the config file.
     */
    protected Dictionary<String, ?> getConfig() {
        return config;
    }
}
