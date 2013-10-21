import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.blades.misc.Printer;
import org.agilewiki.jactor2.core.facilities.Plant;
import org.agilewiki.jactor2.core.messages.SyncRequest;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;

public class Signals extends BladeBase {
    
    public static void main(final String[] _args) throws Exception {
        Plant plant = new Plant();
        try {
            Printer.printlnAReq(plant, "\nCount to 10\n").signal();
            new Signals(new NonBlockingReactor(plant)).countSReq().call();
            Printer.printlnAReq(plant, "").call();
        } finally {
            plant.close();
        }
    }
    
    public Signals(final Reactor _reactor) throws Exception {
        initialize(_reactor);
    }
        
    SyncRequest<Void> countSReq() {
        return new SyncBladeRequest<Void>() {
            protected Void processSyncRequest() throws Exception {
                int i = 0;
                while (i < 10) {
                    i++;
                    Printer.printfAReq(getReactor().getFacility(), "%d\n", i).signal();
                }
                return null;
            }
        };
    }
}
