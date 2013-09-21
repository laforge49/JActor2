import org.agilewiki.jactor2.core.blades.misc.Printer;
import org.agilewiki.jactor2.core.blades.misc.PrinterAgent;
import org.agilewiki.jactor2.core.messages.AsyncRequest;

public class SpeedReport extends PrinterAgent {
    private final String heading;
    private final long ns;
    private final long count;
    
    public static AsyncRequest<Void> startAReq(
            final Printer _printer, 
        final String _heading, 
        final long _ns, 
        final long _count)
            throws Exception {
        return new SpeedReport(_printer, _heading, _ns, _count).startAReq();
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
    
    public AsyncRequest<Void> startAReq() {
        return new AsyncBladeRequest<Void>() {
            @Override
            protected void processAsyncRequest() throws Exception {
                println("");
                println(heading);
                print("Test duration in nanoseconds: %,d%n", ns);
                print("Number of exchanges: %,d%n", count);
                print("Exchanges per second: %,d%n%n", 1000000000L * count / ns);
                processAsyncResponse(null);
            }
        };
    }
}