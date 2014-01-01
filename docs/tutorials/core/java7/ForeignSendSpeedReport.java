import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class ForeignSendSpeedReport {
    public static void main(final String[] _args) throws Exception {
        final long count = 10000000L;
        Plant plant = new Plant();
        try {
            Ponger ponger = new Ponger(new NonBlockingReactor());
            Pinger pinger = new Pinger(new NonBlockingReactor(), ponger);
            AsyncRequest<Void> loopAReq = pinger.loopAReq(count);
            final long before = System.nanoTime();
            loopAReq.call();
            final long after = System.nanoTime();
            final long duration = after - before;
            SpeedReport.print("Foreign Send Timings", duration, count);
        } finally {
            plant.close();
        }
    }
}
