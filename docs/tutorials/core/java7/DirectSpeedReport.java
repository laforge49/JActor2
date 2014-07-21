import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.reactors.BlockingReactor;
import org.agilewiki.jactor2.core.requests.SOp;

public class DirectSpeedReport {
    public static void main(final String[] _args) throws Exception {
        final long count = 2000000000L;
        new Plant();
        try {
            Ponger ponger = new Ponger(new BlockingReactor());
            SOp<Void> loopSOp = new PongerLoop(ponger, count);
            final long before = System.nanoTime();
            loopSOp.call();
            final long after = System.nanoTime();
            final long duration = after - before;
            SpeedReport.print("Direct Timings", duration, count);
        } finally {
            Plant.close();
        }
    }
}
