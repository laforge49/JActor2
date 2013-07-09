package org.apache.karaf;

import org.apache.karaf.tooling.exam.options.LogLevelOption;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.spi.reactors.AllConfinedStagedReactorFactory;
import org.osgi.framework.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private final Logger log = LoggerFactory.getLogger(KarafWithBundleTest.class);

    @Inject
    private BundleContext bundleContext;

    @Configuration
    public Option[] config() {
        return new Option[]{karafDistributionConfiguration().frameworkUrl(
                maven().groupId("org.apache.karaf").artifactId("apache-karaf").type("zip").versionAsInProject())
                .karafVersion("2.2.4").name("Apache Karaf"),

                logLevel(LogLevelOption.LogLevel.INFO),

                mavenBundle("org.agilewiki.jactor2", "jactor-api", "0.0.1-SNAPSHOT"),
                mavenBundle("org.agilewiki.jactor2", "jactor-impl", "0.0.1-SNAPSHOT"),
                mavenBundle("org.agilewiki.jactor2", "jactor-util", "0.0.1-SNAPSHOT"),
                mavenBundle("org.agilewiki.jactor2", "jactor2-osgi", "0.0.1-SNAPSHOT"),
                mavenBundle("org.agilewiki.jactor2", "jactor2-test-iface", "0.0.1-SNAPSHOT"),
                mavenBundle("org.agilewiki.jactor2", "jactor2-kdriver", "0.0.1-SNAPSHOT")
        };
    }

    @Test
    public void test() throws Exception {
        bundleContext.addBundleListener(this);
        bundleContext.addServiceListener(this);
        synchronized (this) {
            wait(30000);
        }
        assertTrue(success);
        Thread.sleep(1000);
    }

    @Override
    public void bundleChanged(BundleEvent bundleEvent) {
        if (bundleEvent.getType() == BundleEvent.STOPPED) {
            if (bundleEvent.getBundle().getSymbolicName().equals("jactor2-kdriver")) {

                try {
                    Thread.sleep(10000);
                } catch (Exception e) {
                }
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
