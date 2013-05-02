package org.agilewiki.jactor.test;

import org.agilewiki.jactor.testIface.Hello;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.ops4j.pax.exam.CoreOptions.*;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class HelloTest {

    @Inject
    private Hello helloService;

    @Inject
    private BundleContext context;

    @Configuration
    public Option[] config() {
        return options(
                mavenBundle("org.agilewiki.pactor", "jactor-api", "0.0.1-SNAPSHOT"),
                mavenBundle("org.agilewiki.pactor", "jactor-impl", "0.0.1-SNAPSHOT"),
                mavenBundle("org.agilewiki.pactor", "jactor-util", "0.0.1-SNAPSHOT"),
                mavenBundle("org.agilewiki.pactor", "jactor-test-iface", "0.0.1-SNAPSHOT"),
                mavenBundle("org.agilewiki.pactor", "jactor-test-service", "0.0.1-SNAPSHOT"),
                junitBundles()
        );
    }

    @Test
    public void getHelloService() {
        assertNotNull(helloService);
        assertEquals("Hello Pax!", helloService.getMessage());
        Bundle bundle = context.getBundle();
    }
}
