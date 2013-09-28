import org.agilewiki.jactor2.core.blades.misc.Printer;
import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class JvmSendSpeedReport {
    public static void main(final String[] _args) throws Exception {
        final long count = 1000000L;
        Facility facility1 = new Facility();
        try {
            Facility facility2 = new Facility();
            try {
                Ponger ponger = new Ponger(new NonBlockingReactor(facility1));
                Pinger pinger = new Pinger(new NonBlockingReactor(facility2), ponger);
                AsyncRequest<Void> loopAReq = pinger.loopAReq(count);
                final long before = System.nanoTime();
                loopAReq.call();
                final long after = System.nanoTime();
                final long duration = after - before;
                Printer printer = new Printer(new IsolationReactor(facility1));
                SpeedReport.startSReq(printer, "JVM Send Timings", duration, count).call();
            } finally {
                facility2.close();
            }
        } finally {
            facility1.close();
        }
    }
}
