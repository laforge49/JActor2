import org.agilewiki.jactor2.core.impl.Plant;

public class CallSpeedReport {
    public static void main(final String[] _args) throws Exception {
        final long count = 1000000L;
        Plant plant = new Plant();
        try {
            Ponger ponger = new Ponger();
            final long before = System.nanoTime();
            long i = 0L;
            while (i < count) {
                i += 1;
                long j = ponger.pingSOp().call();
            }
            final long after = System.nanoTime();
            final long duration = after - before;
            SpeedReport.print("Call Timings", duration, count);
        } finally {
            plant.close();
        }
    }
}
