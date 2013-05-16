package org.apache.karaf;

import org.agilewiki.jactor.testIface.Hello;
import org.apache.karaf.tooling.exam.options.LogLevelOption;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.spi.reactors.AllConfinedStagedReactorFactory;

import javax.inject.Inject;

import static junit.framework.Assert.assertTrue;
import static org.apache.karaf.tooling.exam.options.KarafDistributionOption.karafDistributionConfiguration;
import static org.apache.karaf.tooling.exam.options.KarafDistributionOption.logLevel;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;

/**
 * from http://karaf.apache.org/manual/latest-2.3.x/developers-guide/writing-tests.html
 */
@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(AllConfinedStagedReactorFactory.class)
public class VersionAsInProjectKarafTest {

    @Inject
    private Hello helloService;

    @Configuration
    public Option[] config() {
        return new Option[]{karafDistributionConfiguration().frameworkUrl(
                maven().groupId("org.apache.karaf").artifactId("apache-karaf").type("zip").versionAsInProject())
                .karafVersion("2.2.4").name("Apache Karaf"),

                logLevel(LogLevelOption.LogLevel.DEBUG),

                mavenBundle("org.agilewiki.jactor", "jactor-api", "0.0.1-SNAPSHOT"),
                mavenBundle("org.agilewiki.jactor", "jactor-impl", "0.0.1-SNAPSHOT"),
                mavenBundle("org.agilewiki.jactor", "jactor-util", "0.0.1-SNAPSHOT"),
                mavenBundle("org.agilewiki.jactor", "jactor-test-iface", "0.0.1-SNAPSHOT"),
                mavenBundle("org.agilewiki.jactor", "jactor-test-service", "0.0.1-SNAPSHOT")

        };
    }

    @Test
    public void test() throws Exception {
        assertNotNull(helloService);
        assertEquals("Hello Pax!", helloService.getMessage());
    }
}
