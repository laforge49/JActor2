package org.agilewiki.jactor2.core.messaging;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.processing.IsolationMessageProcessor;
import org.agilewiki.jactor2.core.processing.MessageProcessor;
import org.agilewiki.jactor2.core.processing.NonBlockingMessageProcessor;
import org.agilewiki.jactor2.core.threading.Facility;

/**
 * Test code.
 */
public class Test4 extends TestCase {
    public void testb() throws Exception {
        final Facility facility = new Facility();
        final MessageProcessor messageProcessor = new NonBlockingMessageProcessor(facility);
        new Actor4(messageProcessor).hi4SReq().call();
        facility.close();
    }

    public void testd() throws Exception {
        final Facility facility = new Facility();
        new Actor4(new IsolationMessageProcessor(facility)).hi4SReq().call();
        facility.close();
    }
}
