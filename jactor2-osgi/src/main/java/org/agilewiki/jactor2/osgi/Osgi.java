package org.agilewiki.jactor2.osgi;

import org.agilewiki.jactor2.core.messaging.AsyncRequest;
import org.agilewiki.jactor2.core.messaging.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.processing.MessageProcessor;
import org.agilewiki.jactor2.core.processing.NonBlockingMessageProcessor;
import org.agilewiki.jactor2.core.threading.ModuleContext;
import org.agilewiki.jactor2.util.durable.Durables;
import org.agilewiki.jactor2.util.durable.incDes.Root;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.Version;

/**
 * A collection of static methods for integration with OSGi.
 */
final public class Osgi {

    /**
     * Returns the BundleContext saved in the bundleContext property of a ModuleContext.
     *
     * @param _moduleContext The processing factory.
     * @return The BundleContext.
     */
    public static BundleContext getBundleContext(final ModuleContext _moduleContext) {
        return (BundleContext) _moduleContext.getProperty("bundleContext");
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

    /**
     * Returns the OsgiFactoryLocator associated with a processing.
     *
     * @param _messageProcessor The processing.
     * @return The OsgiFactoryLocator.
     */
    public static OsgiFactoryLocator getOsgiFactoryLocator(final MessageProcessor _messageProcessor) {
        return (OsgiFactoryLocator) Durables.getFactoryLocator(_messageProcessor);
    }

    /**
     * Returns the OsgiFactoryLocator associated with a processing factory.
     *
     * @param _moduleContext The processing factory.
     * @return The OsgiFactoryLocator.
     */
    public static OsgiFactoryLocator getOsgiFactoryLocator(final ModuleContext _moduleContext) {
        return (OsgiFactoryLocator) Durables.getFactoryLocator(_moduleContext);
    }

    /**
     * Returns a filter for locating the factory locator service of another bundle.
     *
     * @param _bundleContext The current bundle context.
     * @param _bundleName    The symbolic name of the bundle of the desired factory locator service.
     * @param _niceVersion   The nice form of the version of the bundle of the desired factory locator service.
     * @return The filter.
     */
    public static Filter factoryLocatorFilter(final BundleContext _bundleContext,
                                              final String _bundleName,
                                              final String _niceVersion) throws Exception {
        return _bundleContext.createFilter("(&" +
                "(objectClass=org.agilewiki.jactor2.osgi.OsgiFactoryLocator)" +
                "(&(bundleName=" + _bundleName + ")(bundleVersion=" + _niceVersion + "))" +
                ")");
    }

    /**
     * Returns a request to create a copy of a root bound to the factory locator that can deserialize it.
     *
     * @param _root The root.
     * @return A copy of the root with the appropriate processing.
     */
    public static AsyncRequest<Root> contextCopyReq(final Root _root) throws Exception {
        return new AsyncRequest<Root>(_root.getMessageProcessor()) {
            AsyncRequest dis = this;

            @Override
            public void processRequest() throws Exception {
                String location = _root.getBundleLocation();
                BundleContext bundleContext = getBundleContext(_root.getMessageProcessor().getModuleContext());
                Bundle bundle = bundleContext.installBundle(location);
                bundle.start();
                Version version = bundle.getVersion();
                LocateService<OsgiFactoryLocator> locateService = new LocateService<OsgiFactoryLocator>(
                        _root.getMessageProcessor(), OsgiFactoryLocator.class.getName());
                locateService.getReq().send(_root.getMessageProcessor(), new AsyncResponseProcessor<OsgiFactoryLocator>() {
                    @Override
                    public void processAsyncResponse(OsgiFactoryLocator response) throws Exception {
                        MessageProcessor newMessageProcessor = new NonBlockingMessageProcessor(response.getModuleContext());
                        _root.copyReq(newMessageProcessor).send(_root.getMessageProcessor(), dis);
                    }
                });
            }
        };
    }
}
