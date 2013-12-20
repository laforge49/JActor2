import org.agilewiki.jactor2.core.blades.misc.Printer;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class FacilitySendSpeedReport {
    public static void main(final String[] _args) throws Exception {
        final long count = 10000000L;
        Plant plant = new Plant();
        try {
            Ponger ponger = new Ponger(new NonBlockingReactor(plant));
            Pinger pinger = new Pinger(new NonBlockingReactor(plant), ponger);
            AsyncRequest<Void> loopAReq = pinger.loopAReq(count);
            final long before = System.nanoTime();
            loopAReq.call();
            final long after = System.nanoTime();
            final long duration = after - before;
            SpeedReport.startAReq(plant, "Facility Send Timings", duration, count).call();
        } finally {
            plant.close();
        }
    }
}
