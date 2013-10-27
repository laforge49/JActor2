import org.agilewiki.jactor2.core.blades.misc.Printer;
import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.facilities.Plant;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class JvmSendSpeedReport {
    public static void main(final String[] _args) throws Exception {
        final long count = 1000000L;
        Plant plant = new Plant();
        try {
            Facility facility2 = plant.createFacilityAReq("facility2").call();
            Ponger ponger = new Ponger(new NonBlockingReactor(plant));
            Pinger pinger = new Pinger(new NonBlockingReactor(facility2), ponger);
            AsyncRequest<Void> loopAReq = pinger.loopAReq(count);
            final long before = System.nanoTime();
            loopAReq.call();
            final long after = System.nanoTime();
            final long duration = after - before;
            SpeedReport.startAReq(plant, "JVM Send Timings", duration, count).call();
        } finally {
            plant.close();
        }
    }
}
