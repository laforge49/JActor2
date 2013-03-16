package agilewiki.pactor.pingpong;

import org.agilewiki.pactor.Mailbox;
import org.agilewiki.pactor.Request;
import org.agilewiki.pactor.ResponseProcessor;

/**
 * Receives Pings, and send Pongs back.
 */
public class Ponger {
    /** Ponger mailbox */
    private final Mailbox mailbox;

    /** Some mutable data of Ponger, which must be access in a thread-safe way. */
    private int pings;

    /** A Ping request, targeted at Ponger. */
    private class PingRequest extends Request<String> {
        private final String from;

        public PingRequest(final Mailbox mbox, final String _from) {
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
    public Ponger(final Mailbox mbox) {
        mailbox = mbox;
    }

    /** Sends a ping(String) request to the Ponger. Blocks and returns response. */
    public String ping(final String from) throws Exception {
        return new PingRequest(mailbox, from).pend();
    }
}
