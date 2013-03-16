package agilewiki.pactor.pingpong;

import org.agilewiki.pactor.Mailbox;
import org.agilewiki.pactor.Request;
import org.agilewiki.pactor.ResponseProcessor;

/**
 * The Pinger's job is to hammer the Ponger with ping() request,
 * to count how many can be done in one second.
 */
public class Pinger {
    /* Hammer request result */
    public static final class HammerResult {
        /** Number of pings sent. */
        private final int pings;

        /** Duration. */
        private final double duration;

        /** Constructor */
        public HammerResult(final int _pings, final double _duration) {
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
    private class HammerRequest extends Request<HammerResult> {
        /** The Ponger to hammer. */
        private final Ponger ponger;

        /** Creates a hammer request, with the targeted Ponger. */
        public HammerRequest(final Mailbox mbox, final Ponger _ponger) {
            super(mbox);
            ponger = _ponger;
        }

        /** Process the hammer request. */
        @Override
        public void processRequest(
                final ResponseProcessor<HammerResult> responseProcessor)
                throws Exception {
            int count = 0;
            final long start = System.nanoTime();
            final long delay = PING_FOR_IN_MS * 1000000L;
            long now;
            while ((now = System.nanoTime()) - start < delay) {
                ponger.ping(name);
                count++;
            }
            responseProcessor.processResponse(new HammerResult(count,
                    ((now - start) / 1000000000.0d)));
        }
    }

    /** Creates a Pinger, with it's own mailbox and name. */
    public Pinger(final Mailbox mbox, final String _name) {
        mailbox = mbox;
        name = _name;
    }

    /** Tells the pinger to hammer the Ponger. Describes the speed in the result. */
    public HammerResult hammer(final Ponger ponger) throws Exception {
        return new HammerRequest(mailbox, ponger).pend();
    }
}
