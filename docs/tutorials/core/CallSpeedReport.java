import org.agilewiki.jactor2.core.blades.misc.Printer;
import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class CallSpeedReport {
    public static void main(final String[] _args) throws Exception {
        final long count = 1000000L;
        Facility facility = new Facility();
        try {
            Ponger ponger = new Ponger(new NonBlockingReactor(facility));
            final long before = System.nanoTime();
            long i = 0L;
            while (i < count) {
                i += 1;
                long j = ponger.pingSReq().call();
            }
            final long after = System.nanoTime();
            final long duration = after - before;
            Printer printer = Printer.stdoutAReq(facility).call();
            SpeedReport.startSReq(printer, "Call Timings", duration, count).call();
        } finally {
            facility.close();
        }
    }
}
