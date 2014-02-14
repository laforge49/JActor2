import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.requests.SyncRequest;

public class Signals extends NonBlockingBladeBase {
    
    public static void main(final String[] _args) throws Exception {
        new Plant();
        try {
            Signals signals = new Signals();
            int i = 0;
            while (i < 10) {
                i++;
                signals.printSReq(i).signal();
            }
            signals.getReactor().nullSReq().call();
        } finally {
            Plant.close();
        }
    }
        
    SyncRequest<Void> printSReq(final Integer _i) {
        return new SyncBladeRequest<Void>() {
            public Void processSyncRequest() {
                System.out.println(_i);
                return null;
            }
        };
    }
}
