import org.agilewiki.jactor2.core.blades.misc.Printer;
import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.messages.SyncRequest;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class LocalSpeedReport {
    public static void main(final String[] _args) throws Exception {
        final long count = 500000000L;
        Facility facility = new Facility();
        try {
            Ponger ponger = new Ponger(new NonBlockingReactor(facility));
            SyncRequest<Void> startSReq = PongerAgent.startSReq(ponger, count);
            final long before = System.nanoTime();
            startSReq.call();
            final long after = System.nanoTime();
            final long duration = after - before;
            SpeedReport.startAReq(facility, "Local Timings", duration, count).call();
        } finally {
            facility.close();
        }
    }
}
