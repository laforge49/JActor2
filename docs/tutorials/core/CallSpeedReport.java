import org.agilewiki.jactor2.core.blades.misc.Printer;
import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class CallSpeedReport {
    public static void main(final String[] _args) throws Exception {
        final long count = 1000000L;
        Facility facility = new Facility();
        try {
            final long t0 = System.nanoTime();
            long i = 0L;
            while (i < count) {
                i += 1;
            }
            final long t1 = System.nanoTime();
            Ponger ponger = new Ponger(new NonBlockingReactor(facility));
            final long t10 = System.nanoTime();
            final long d0 = t1 - t0;
            i = 0L;
            while (i < count) {
                i += 1;
                long j = ponger.pingSReq().call();
            }
            final long t11 = System.nanoTime();
            final long d1 = t11 - t10;
            Printer printer = new Printer(new IsolationReactor(facility));
            SpeedReport.startSReq(printer, "Speed Report Validation", d1 - d0, count).call();
        } finally {
            facility.close();
        }
    }
}
