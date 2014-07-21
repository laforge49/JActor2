import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.requests.AOp;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class ForeignSendSpeedReport {
    public static void main(final String[] _args) throws Exception {
        final long count = 10000000L;
        new Plant();
        try {
            Ponger ponger = new Ponger();
            Pinger pinger = new Pinger(new NonBlockingReactor(), ponger);
            AOp<Void> loopAOp = pinger.loopAOp(count);
            final long before = System.nanoTime();
            loopAOp.call();
            final long after = System.nanoTime();
            final long duration = after - before;
            SpeedReport.print("Foreign Send Timings", duration, count);
        } finally {
            Plant.close();
        }
    }
}
