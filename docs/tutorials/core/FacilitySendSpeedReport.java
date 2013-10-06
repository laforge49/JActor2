import org.agilewiki.jactor2.core.blades.misc.Printer;
import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class FacilitySendSpeedReport {
    public static void main(final String[] _args) throws Exception {
        final long count = 10000000L;
        Facility facility = new Facility();
        try {
            Ponger ponger = new Ponger(new NonBlockingReactor(facility));
            Pinger pinger = new Pinger(new NonBlockingReactor(facility), ponger);
            AsyncRequest<Void> loopAReq = pinger.loopAReq(count);
            final long before = System.nanoTime();
            loopAReq.call();
            final long after = System.nanoTime();
            final long duration = after - before;
            Printer printer = Printer.stdoutSReq(facility).call();
            SpeedReport.startSReq(printer, "Facility Send Timings", duration, count).call();
        } finally {
            facility.close();
        }
    }
}
