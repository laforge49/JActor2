package org.apache.karaf;

import org.apache.karaf.tooling.exam.options.LogLevelOption;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.TestProbeBuilder;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.junit.ProbeBuilder;
import org.ops4j.pax.exam.spi.reactors.AllConfinedStagedReactorFactory;
import org.osgi.framework.*;

import javax.inject.Inject;

import static org.apache.karaf.tooling.exam.options.KarafDistributionOption.karafDistributionConfiguration;
import static org.apache.karaf.tooling.exam.options.KarafDistributionOption.logLevel;
import static org.junit.Assert.assertTrue;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;

/**
 * from http://karaf.apache.org/manual/latest-2.3.x/developers-guide/writing-tests.html
 * And then modified using
 * http://svn.apache.org/viewvc/karaf/trunk/tooling/exam/regression/src/test/java/org/apache/karaf/tooling/exam/regression/KarafWithBundleTest.java?view=markup
 */
@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(AllConfinedStagedReactorFactory.class)
public class KarafWithBundleTest implements BundleListener, ServiceListener {
    private boolean success;

    /*
    The code below causes this:
    java.lang.NoClassDefFoundError: org/osgi/framework/BundleListener

    @ProbeBuilder
    public TestProbeBuilder probeConfiguration(TestProbeBuilder probe) {
        probe.setHeader(Constants.DYNAMICIMPORT_PACKAGE, "*;status=provisional");
        return probe;
    }
    */

    @Inject
    private BundleContext bundleContext;

    @Configuration
    public Option[] config() {
        return new Option[]{karafDistributionConfiguration().frameworkUrl(
                maven().groupId("org.apache.karaf").artifactId("apache-karaf").type("zip").versionAsInProject())
                .karafVersion("2.2.4").name("Apache Karaf"),

                logLevel(LogLevelOption.LogLevel.INFO),
                mavenBundle("org.agilewiki.jactor", "jactor-api", "0.0.1-SNAPSHOT"),
                mavenBundle("org.agilewiki.jactor", "jactor-impl", "0.0.1-SNAPSHOT"),
                mavenBundle("org.agilewiki.jactor", "jactor-util", "0.0.1-SNAPSHOT"),
                mavenBundle("org.agilewiki.jactor", "jactor-test-iface", "0.0.1-SNAPSHOT"),
                mavenBundle("org.agilewiki.jactor", "jactor-kdriver", "0.0.1-SNAPSHOT")
        };
    }

    @Test
    public void test() throws Exception {
        bundleContext.addBundleListener(this);
        bundleContext.addServiceListener(this);
        synchronized (this) {
            wait(20000);
        }
        assertTrue(success);
    }

    @Override
    public void bundleChanged(BundleEvent bundleEvent) {
        if (bundleEvent.getType() == BundleEvent.STOPPED) {
            if (bundleEvent.getBundle().getSymbolicName().equals("jactor-kdriver")) {
                synchronized (this) {
                    this.notify();
                }
            }
        }
    }

    @Override
    public void serviceChanged(ServiceEvent serviceEvent) {
        if (serviceEvent.getType() != ServiceEvent.REGISTERED)
            return;
        ServiceReference serviceReference = serviceEvent.getServiceReference();
        String t = (String) serviceReference.getProperty("kdriverSuccess");
        if (t != null) {
            success = true;
            synchronized (this) {
                this.notify();
            }
        }
    }
}
