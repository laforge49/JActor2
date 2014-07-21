import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.requests.AOp;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class NativeSendSpeedReport {
    public static void main(final String[] _args) throws Exception {
        final long count = 100000000L;
        new Plant();
        try {
            Ponger ponger = new Ponger();
            NonBlockingReactor sharedReactor = (NonBlockingReactor) ponger.getReactor();
            Pinger pinger = new Pinger(sharedReactor, ponger);
            AOp<Void> loopAReq = pinger.loopAOp(count);
            final long before = System.nanoTime();
            loopAOp.call();
            final long after = System.nanoTime();
            final long duration = after - before;
            SpeedReport.print("Native Send Timings", duration, count);
        } finally {
            Plant.close();
        }
    }
}
