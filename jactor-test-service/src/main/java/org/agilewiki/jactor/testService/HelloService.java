package org.agilewiki.jactor.testService;

import org.agilewiki.jactor.api.ActorBase;
import org.agilewiki.jactor.api.Mailbox;
import org.agilewiki.jactor.api.Transport;
import org.agilewiki.jactor.testIface.Hello;
import org.agilewiki.jactor.util.osgi.serviceTracker.JAServiceTracker;
import org.agilewiki.jactor.util.osgi.serviceTracker.ServiceChangeReceiver;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class HelloService extends ActorBase implements Hello, ServiceChangeReceiver {
    private final Logger logger = LoggerFactory.getLogger(HelloService.class);
    private BundleContext context;

    public HelloService(final BundleContext _context, Mailbox mailbox) throws Exception {
        initialize(mailbox);
        context = _context;
        String filter = "(&(objectClass=org.agilewiki.jactor.util.durable.FactoryLocator)(&(bundleName=jactor-util)(bundleVersion=0.0.1.SNAPSHOT)))";
        JAServiceTracker<Object> st = new JAServiceTracker(getMailbox(), context.createFilter(filter));
        Map<ServiceReference, Object> m = st.startReq(this).call();
        System.out.println("..................."+m.size());
    }

    @Override
    public String getMessage() throws Exception {
        //Bundle jactorUtil = context.getBundle("mvn:org.agilewiki.jactor/jactor-util/0.0.1-SNAPSHOT");
        //jactorUtil.stop();
        //jactorUtil.start();
        System.out.println("!!!!!!!!!!!!! ");
        System.out.println(context.getBundle().getVersion());
        System.out.println(context.getBundle().getLocation());
        logger.info("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        return "Hello Pax!";
    }

    @Override
    public void serviceChange(ServiceEvent _event, Map _tracked, Transport _transport) throws Exception {
        System.out.println(">>>>>>>>>>>>>>>>>> Service change <<<<<<<<<<<<<<<<<< " + _tracked.size() + " -> "+_event.getType());
    }
}
