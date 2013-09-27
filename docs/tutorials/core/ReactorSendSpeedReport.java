import org.agilewiki.jactor2.core.blades.misc.Printer;
import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class ReactorSendSpeedReport {
    public static void main(final String[] _args) throws Exception {
        final long count = 100000000L;
        Facility facility = new Facility();
        try {
            NonBlockingReactor sharedReactor = new NonBlockingReactor(facility);
            Ponger ponger = new Ponger(sharedReactor);
            Pinger pinger = new Pinger(sharedReactor, ponger);
            AsyncRequest<Void> loopAReq = pinger.loopAReq(count);
            final long before = System.nanoTime();
            loopAReq.call();
            final long after = System.nanoTime();
            final long duration = after - before;
            Printer printer = new Printer(new IsolationReactor(facility));
            SpeedReport.startSReq(printer, "Local Send Timings", duration, count).call();
        } finally {
            facility.close();
        }
    }
}
