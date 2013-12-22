import org.agilewiki.jactor2.core.blades.misc.Printer;
import org.agilewiki.jactor2.core.plant.BasicPlant;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class CallSpeedReport {
    public static void main(final String[] _args) throws Exception {
        final long count = 1000000L;
        BasicPlant plant = new Plant();
        try {
            Ponger ponger = new Ponger(new NonBlockingReactor(plant));
            final long before = System.nanoTime();
            long i = 0L;
            while (i < count) {
                i += 1;
                long j = ponger.pingSReq().call();
            }
            final long after = System.nanoTime();
            final long duration = after - before;
            SpeedReport.startAReq(plant, "Call Timings", duration, count).call();
        } finally {
            plant.close();
        }
    }
}
