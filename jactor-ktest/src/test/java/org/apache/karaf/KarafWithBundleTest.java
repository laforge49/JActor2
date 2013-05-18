package org.apache.karaf;

import org.apache.karaf.tooling.exam.options.LogLevelOption;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.spi.reactors.AllConfinedStagedReactorFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;

import javax.inject.Inject;

import static org.apache.karaf.tooling.exam.options.KarafDistributionOption.karafDistributionConfiguration;
import static org.apache.karaf.tooling.exam.options.KarafDistributionOption.logLevel;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;

/**
 * from http://karaf.apache.org/manual/latest-2.3.x/developers-guide/writing-tests.html
 * And then modified using
 * http://svn.apache.org/viewvc/karaf/trunk/tooling/exam/regression/src/test/java/org/apache/karaf/tooling/exam/regression/KarafWithBundleTest.java?view=markup
 */
@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(AllConfinedStagedReactorFactory.class)
public class KarafWithBundleTest implements BundleListener {

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
                mavenBundle("org.agilewiki.jactor", "jactor-test-service", "0.0.1-SNAPSHOT")
        };
    }

    @Test
    public void test() throws Exception {
        System.out.println("bbbbbbbbbbbbbbbbbbbbbbbbbbb");
        bundleContext.addBundleListener(this);
        synchronized(this) {
            wait(10000);
        }
        System.out.println("ccccccccccccccccccccccccccc");
    }

    @Override
    public void bundleChanged(BundleEvent bundleEvent) {
        if ( bundleEvent.getType() == BundleEvent.STOPPED) {
            if (bundleEvent.getBundle().getSymbolicName().equals("jactor-test-service")) {
                System.out.println("zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz " + bundleEvent.getBundle().getSymbolicName());
                synchronized(this) {
                    this.notify();
                }
            }
        }
    }
}
