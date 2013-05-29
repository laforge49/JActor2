// Java
/*
 * Licensed to the Apache Software Foundation (ASF)
 * ...
 */
package org.apache.felix.karaf.shell.itests;

import org.apache.felix.karaf.testing.AbstractIntegrationTest;
import org.apache.felix.karaf.testing.Helper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.service.blueprint.container.BlueprintContainer;
import org.osgi.service.command.CommandProcessor;
import org.osgi.service.command.CommandSession;

import static org.junit.Assert.assertNotNull;
import static org.ops4j.pax.exam.CoreOptions.*;
import static org.ops4j.pax.exam.OptionUtils.combine;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.scanFeatures;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.workingDirectory;

@RunWith(JUnit4TestRunner.class)
public class FeaturesTest extends AbstractIntegrationTest {

    @Test
    public void testFeatures() throws Exception {
        // Make sure the command services are available
        assertNotNull(getOsgiService(BlueprintContainer.class, "osgi.blueprint.container.symbolicname=org.apache.felix.karaf.shell.obr", 20000));
        assertNotNull(getOsgiService(BlueprintContainer.class, "osgi.blueprint.container.symbolicname=org.apache.felix.karaf.shell.wrapper", 20000));
        // Run some commands to make sure they are installed properly
        CommandProcessor cp = getOsgiService(CommandProcessor.class);
        CommandSession cs = cp.createSession(System.in, System.out, System.err);
        cs.execute("obr:listUrl");
        cs.execute("wrapper:install --help");
        cs.close();
    }

    @Configuration
    public static Option[] configuration() throws Exception {
        return combine(
                // Default karaf environment
                Helper.getDefaultOptions(
                        // this is how you set the default log level when using pax logging (logProfile)
                        systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level").value("DEBUG")),

                // add two features
                scanFeatures(
                        maven().groupId("org.apache.felix.karaf").artifactId("apache-felix-karaf").type("xml").classifier("features").versionAsInProject(),
                        "obr", "wrapper"
                ),

                workingDirectory("target/paxrunner/features/"),

                waitForFrameworkStartup(),

                // Test on both equinox and felix
                equinox(), felix()
        );
    }

}
