package org.agilewiki.jactor.testService;

import org.agilewiki.paosgi.testIface.Hello;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloService implements Hello {
    private final Logger logger = LoggerFactory.getLogger(HelloService.class);
    private BundleContext context;

    public HelloService(final BundleContext _context) {
        context = _context;
    }

    @Override
    public String getMessage() {
        System.out.println("!!!!!!!!!!!!!");
        System.out.println(context.getBundle().getVersion());
        System.out.println(context.getBundle().getLocation());
        logger.info("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        return "Hello Pax!";
    }
}
