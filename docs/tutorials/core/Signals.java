import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.blades.misc.Printer;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.messages.SyncRequest;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class Signals extends NonBlockingBladeBase {
    
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
    
    public Signals(final NonBlockingReactor _reactor) throws Exception {
        initialize(_reactor);
    }
        
    SyncRequest<Void> countSReq() {
        return new SyncBladeRequest<Void>() {
            public Void processSyncRequest() throws Exception {
                int i = 0;
                while (i < 10) {
                    i++;
                    Printer.printfAReq(getReactor().getPlant(), "%d\n", i).signal();
                }
                return null;
            }
        };
    }
}
