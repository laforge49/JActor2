import org.agilewiki.jactor2.core.blades.misc.Printer;
import org.agilewiki.jactor2.core.blades.misc.PrinterAgent;

public class SpeedReport extends PrinterAgent {
    private final String heading;
    private final long ns;
    private final long count;

    public SpeedReport(final Printer _printer, final String _heading, final long _ns, final long _count)
            throws Exception {
        super(_printer);
        heading = _heading;
        ns = _ns;
        count = _count;
    }
    
    public AsyncBladeRequest<Void> startAReq() {
        return new AsyncBladeRequest<Void>() {
            @Override
            protected void processAsyncRequest() throws Exception {
                println(heading);
                print("Test duration in nanoseconds: %,d%n", ns);
                print("Number of exchanges: %,d%n", count);
                processAsyncResponse(null);
            }
        };
    }
}