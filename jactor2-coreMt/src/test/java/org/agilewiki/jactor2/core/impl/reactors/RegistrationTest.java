package org.agilewiki.jactor2.core.impl.reactors;

import org.agilewiki.jactor2.core.blades.NamedBlade;
import org.agilewiki.jactor2.core.blades.pubSub.SubscribeAReq;
import org.agilewiki.jactor2.core.impl.CallTestBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.reactors.Facility;
import org.agilewiki.jactor2.core.reactors.RegistrationNotification;

public class RegistrationTest extends CallTestBase {
    public void test1() throws Exception {
        new Plant();
        try {
            final NamedBlade namedBlade = new NamedBlade() {
                @Override
                public String getName() {
                    return "FooBar";
                }
            };
            final Facility facility = new Facility("TestFacility");
            call(new SubscribeAReq<RegistrationNotification>(
                    facility.registrationNotifier, facility) {
                @Override
                protected void processContent(
                        final RegistrationNotification registrationNotification) {
                    if (registrationNotification.isRegistration())
                        System.out.println("registered: "
                                + registrationNotification.name);
                    else
                        System.out.println("unregistered: "
                                + registrationNotification.name);
                }
            });
            call(facility.registerBladeSReq(namedBlade));
            call(facility.unregisterBladeSReq("TestFacility"));
            call(facility.unregisterBladeSReq("FooBar"));
        } finally {
            Plant.close();
        }
    }
}
