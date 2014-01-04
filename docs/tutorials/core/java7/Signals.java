import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.requests.SyncRequest;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class Signals extends NonBlockingBladeBase {
    
    public static void main(final String[] _args) throws Exception {
        Plant plant = new Plant();
        try {
            System.out.println("\nCount to 10\n");
            new Signals(new NonBlockingReactor()).countSReq().call();
            System.out.println("");
        } finally {
            plant.close();
        }
    }
    
    public Signals(final NonBlockingReactor _reactor) throws Exception {
        super(_reactor);
    }
        
    SyncRequest<Void> countSReq() {
        return new SyncBladeRequest<Void>() {
            public Void processSyncRequest() throws Exception {
                int i = 0;
                while (i < 10) {
                    i++;
                    System.out.println(i);
                }
                return null;
            }
        };
    }
}
