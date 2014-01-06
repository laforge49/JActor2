import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class NativeSendSpeedReport {
    public static void main(final String[] _args) throws Exception {
        final long count = 100000000L;
        new Plant();
        try {
            Ponger ponger = new Ponger();
            NonBlockingReactor sharedReactor = ponger.getReactor();
            Pinger pinger = new Pinger(sharedReactor, ponger);
            AsyncRequest<Void> loopAReq = pinger.loopAReq(count);
            final long before = System.nanoTime();
            loopAReq.call();
            final long after = System.nanoTime();
            final long duration = after - before;
            SpeedReport.print("Native Send Timings", duration, count);
        } finally {
            Plant.close();
        }
    }
}
