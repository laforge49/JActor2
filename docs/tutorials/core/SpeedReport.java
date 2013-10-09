import org.agilewiki.jactor2.core.blades.misc.Printer;
import org.agilewiki.jactor2.core.blades.misc.SyncPrinterRequest;
import org.agilewiki.jactor2.core.facilities.AsyncFacilityRequest;
import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;

public class SpeedReport extends SyncPrinterRequest {
    
    public static AsyncRequest<Void> startAReq(
            final Facility _facility,
            final String _heading, 
            final long _ns, 
            final long _count) {
        return new AsyncFacilityRequest<Void>(_facility) {
            AsyncResponseProcessor dis = this;
            
            @Override
            public void processAsyncRequest() throws Exception {
                Printer printer = local(Printer.stdoutSReq(_facility));
                SpeedReport sr = new SpeedReport(printer, _heading, _ns, _count);
                send(sr, dis);
            }
        };
    }

    private final String heading;
    private final long ns;
    private final long count;
    
    private SpeedReport(final Printer _printer, 
            final String _heading, 
            final long _ns, 
            final long _count) throws Exception {
        super(_printer);
        heading = _heading;
        ns = _ns;
        count = _count;
    }

    @Override
    protected Void processSyncRequest() throws Exception {
        println("");
        println(heading);
        printf("Test duration in nanoseconds: %,d%n", ns);
        printf("Number of exchanges: %,d%n", count);
        if (ns > 0)
            printf("Exchanges per second: %,d%n%n", 1000000000L * count / ns);
        return null;
    }
}
