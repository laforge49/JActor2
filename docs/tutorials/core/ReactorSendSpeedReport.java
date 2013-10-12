import org.agilewiki.jactor2.core.blades.misc.Printer;
import org.agilewiki.jactor2.core.facilities.Plant;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class ReactorSendSpeedReport {
    public static void main(final String[] _args) throws Exception {
        final long count = 100000000L;
        Plant plant = new Plant();
        try {
            NonBlockingReactor sharedReactor = new NonBlockingReactor(plant);
            Ponger ponger = new Ponger(sharedReactor);
            Pinger pinger = new Pinger(sharedReactor, ponger);
            AsyncRequest<Void> loopAReq = pinger.loopAReq(count);
            final long before = System.nanoTime();
            loopAReq.call();
            final long after = System.nanoTime();
            final long duration = after - before;
            SpeedReport.startAReq(plant, "Reactor Send Timings", duration, count).call();
        } finally {
            plant.close();
        }
    }
}
