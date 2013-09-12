package org.agilewiki.jactor2.core.exceptions;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.processing.IsolationReactor;
import org.agilewiki.jactor2.core.processing.NonBlockingReactor;
import org.agilewiki.jactor2.core.processing.Reactor;
import org.agilewiki.jactor2.core.threading.Facility;

/**
 * Test code.
 */
public class Test2 extends TestCase {
    public void testI() throws Exception {
        System.out.println("testI");
        final Facility facility = new Facility();
        final Reactor reactor = new NonBlockingReactor(facility);
        final ActorA actorA = new ActorA(reactor);
        final ActorB actorB = new ActorB(reactor);
        try {
            actorB.throwRequest(actorA).call();
        } catch (final SecurityException se) {
            facility.close();
            return;
        }
        facility.close();
        throw new Exception("Security exception was not caught");
    }

    public void testIII() throws Exception {
        System.out.println("testIII");
        final Facility facility = new Facility();
        final ActorA actorA = new ActorA(new IsolationReactor(facility));
        final ActorB actorB = new ActorB(new IsolationReactor(facility));
        try {
            actorB.throwRequest(actorA).call();
        } catch (final SecurityException se) {
            facility.close();
            return;
        }
        facility.close();
        throw new Exception("Security exception was not caught");
    }
}
