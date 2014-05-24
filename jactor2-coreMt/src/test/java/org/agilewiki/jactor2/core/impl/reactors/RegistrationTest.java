package org.agilewiki.jactor2.core.impl.reactors;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.blades.NamedBlade;
import org.agilewiki.jactor2.core.blades.pubSub.SubscribeAReq;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.reactors.Facility;
import org.agilewiki.jactor2.core.reactors.RegistrationNotification;

public class RegistrationTest extends TestCase {
    public void test1() throws Exception {
        new Plant();
        try {
            NamedBlade namedBlade = new NamedBlade() {
                @Override
                public String getName() {
                    return "FooBar";
                }
            };
            Facility facility = new Facility("TestFacility");
            new SubscribeAReq<RegistrationNotification>(facility.registrationNotifier, facility) {
                @Override
                protected void processContent(RegistrationNotification registrationNotification) {
                    if (registrationNotification.isRegistration())
                        System.out.println("registered: " + registrationNotification.name);
                    else
                        System.out.println("unregistered: " + registrationNotification.name);
                }
            }.call();
            facility.registerBladeSReq(namedBlade).call();
            facility.unregisterBladeSReq("TestFacility").call();
            Thread.sleep(1000);
        } catch (Exception ex) {
            Plant.close();
        }
    }
}
