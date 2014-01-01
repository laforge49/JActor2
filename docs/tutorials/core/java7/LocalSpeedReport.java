import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.requests.SyncRequest;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class LocalSpeedReport {
    public static void main(final String[] _args) throws Exception {
        final long count = 500000000L;
        Plant plant = new Plant();
        try {
            Ponger ponger = new Ponger(new NonBlockingReactor());
            SyncRequest<Void> loopSReq = new PongerLoop(ponger, count);
            final long before = System.nanoTime();
            loopSReq.call();
            final long after = System.nanoTime();
            final long duration = after - before;
            SpeedReport.print("Local Timings", duration, count);
        } finally {
            plant.close();
        }
    }
}
