package org.agilewiki.jactor2.core.exceptions;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.processing.IsolationMessageProcessor;
import org.agilewiki.jactor2.core.processing.MessageProcessor;
import org.agilewiki.jactor2.core.threading.Facility;

/**
 * Test code.
 */
public class Test1 extends TestCase {
    public void testI() throws Exception {
        final Facility facility = new Facility();
        final MessageProcessor messageProcessor = new IsolationMessageProcessor(facility);
        final ActorA actorA = new ActorA(messageProcessor);
        try {
            actorA.throwRequest.call();
        } catch (final SecurityException se) {
            facility.close();
            return;
        }
        throw new Exception("Security exception was not caught");
    }

}
