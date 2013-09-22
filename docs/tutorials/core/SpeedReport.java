import org.agilewiki.jactor2.core.blades.misc.Printer;
import org.agilewiki.jactor2.core.blades.misc.PrinterAgent;
import org.agilewiki.jactor2.core.messages.SyncRequest;

public class SpeedReport extends PrinterAgent {
    private final String heading;
    private final long ns;
    private final long count;
    
    public static SyncRequest<Void> startSReq(
            final Printer _printer, 
        final String _heading, 
        final long _ns, 
        final long _count)
            throws Exception {
        return new SpeedReport(_printer, _heading, _ns, _count).startSReq();
    }

    public SpeedReport(
        final Printer _printer, 
        final String _heading, 
        final long _ns, 
        final long _count)
            throws Exception {
        super(_printer);
        heading = _heading;
        ns = _ns;
        count = _count;
    }
    
    public SyncRequest<Void> startSReq() {
        return new SyncBladeRequest<Void>() {
            @Override
            protected Void processSyncRequest() throws Exception {
                println("");
                println(heading);
                printf("Test duration in nanoseconds: %,d%n", ns);
                printf("Number of exchanges: %,d%n", count);
                printf("Exchanges per second: %,d%n%n", 1000000000L * count / ns);
                return null;
            }
        };
    }
}