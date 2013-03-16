package agilewiki.pactor.pingpong;

import org.agilewiki.pactor.Mailbox;
import org.agilewiki.pactor.Request;
import org.agilewiki.pactor.ResponseProcessor;

/**
 * The Pinger's job is to hammer the Ponger with ping() request,
 * to count how many can be done in one second.
 */
public class Pinger2 {
    /* Hammer request result */
    public static final class HammerResult2 {
        /** Number of pings sent. */
        private final int pings;

        /** Duration. */
        private final double duration;

        /** Constructor */
        public HammerResult2(final int _pings, final double _duration) {
            pings = _pings;
            duration = _duration;
        }

        /** toString */
        @Override
        public String toString() {
            return "Sent " + pings + " pings in " + duration + " seconds";
        }

        /** Number of pings sent. */
        public int pings() {
            return pings;
        }

        /** Duration. */
        public double duration() {
            return duration;
        }
    }

    /** How long to send pings? */
    private static final long PING_FOR_IN_MS = 1000L;

    /** The Pinger's mailbox. */
    private final Mailbox mailbox;

    /** The name of the pinger. */
    private final String name;

    /** A Hammer request, targeted at Pinger. */
    private class HammerRequest2 extends Request<HammerResult2> {
        /** The waiting time */
        private static final long DELAY = PING_FOR_IN_MS * 1000000L;

        /** The Ponger to hammer. */
        private final Ponger2 ponger;

        /** The number of pings sent. */
        private int count;

        /** The start time of the hammer call. */
        private long start;

        /** ResponseProcessor for benchmark results */
        private ResponseProcessor<HammerResult2> responseProcessor;

        /** Creates a hammer request, with the targeted Ponger. */
        public HammerRequest2(final Mailbox mbox, final Ponger2 _ponger) {
            super(mbox);
            ponger = _ponger;
        }

        private void ping() throws Exception {
            final long now = System.nanoTime();
            count++;
            if (now - start < DELAY) {
                ponger.ping(Pinger2.this, new ResponseProcessor<String>() {
                    @Override
                    public void processResponse(final String response)
                            throws Exception {
                        ping();
                    }
                });
            } else {
                responseProcessor.processResponse(new HammerResult2(count,
                        ((now - start) / 1000000000.0d)));
            }
        }

        /** Process the hammer request. */
        @Override
        public void processRequest(
                final ResponseProcessor<HammerResult2> _responseProcessor)
                throws Exception {
            count = 0;
            start = System.nanoTime();
            responseProcessor = _responseProcessor;
            ping();
        }
    }

    /** Creates a Pinger, with it's own mailbox and name. */
    public Pinger2(final Mailbox mbox, final String _name) {
        mailbox = mbox;
        name = _name;
    }

    /** Tells the pinger to hammer the Ponger. Describes the speed in the result. */
    public HammerResult2 hammer(final Ponger2 ponger) throws Exception {
        return new HammerRequest2(getMailbox(), ponger).pend();
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the mailbox
     */
    public Mailbox getMailbox() {
        return mailbox;
    }
}
