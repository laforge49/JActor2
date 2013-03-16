package agilewiki.pactor.pingpong;

import org.agilewiki.pactor.Mailbox;
import org.agilewiki.pactor.Request;
import org.agilewiki.pactor.ResponseProcessor;

/**
 * Receives Pings, and send Pongs back.
 */
public class Ponger2 {
    /** Ponger mailbox */
    private final Mailbox mailbox;

    /** Some mutable data of Ponger, which must be access in a thread-safe way. */
    private int pings;

    /** A Ping request, targeted at Ponger. */
    private class PingRequest2 extends Request<String> {
        private final String from;

        public PingRequest2(final Mailbox mbox, final String _from) {
            super(mbox);
            from = _from;
        }

        /** Processes the ping(String) request, from within the Thread of the Ponger. */
        @Override
        public void processRequest(
                final ResponseProcessor<String> responseProcessor)
                throws Exception {
            responseProcessor.processResponse("Pong " + (pings++) + " to "
                    + from + "!");
        }
    }

    /** Creates a Ponger, with it's own mailbox. */
    public Ponger2(final Mailbox mbox) {
        mailbox = mbox;
    }

    /** Sends a ping(String) request to the Ponger. Blocks and returns response. */
    public void ping(final Pinger2 from,
            final ResponseProcessor<String> responseProcessor) throws Exception {
        new PingRequest2(mailbox, from.getName()).reply(from.getMailbox(),
                responseProcessor);
    }
}
