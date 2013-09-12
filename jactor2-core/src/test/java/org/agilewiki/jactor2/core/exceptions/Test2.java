package org.agilewiki.jactor2.core.exceptions;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.processing.IsolationMessageProcessor;
import org.agilewiki.jactor2.core.processing.MessageProcessor;
import org.agilewiki.jactor2.core.processing.NonBlockingMessageProcessor;
import org.agilewiki.jactor2.core.threading.Facility;

/**
 * Test code.
 */
public class Test2 extends TestCase {
    public void testI() throws Exception {
        System.out.println("testI");
        final Facility facility = new Facility();
        final MessageProcessor messageProcessor = new NonBlockingMessageProcessor(facility);
        final ActorA actorA = new ActorA(messageProcessor);
        final ActorB actorB = new ActorB(messageProcessor);
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
        final ActorA actorA = new ActorA(new IsolationMessageProcessor(facility));
        final ActorB actorB = new ActorB(new IsolationMessageProcessor(facility));
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
