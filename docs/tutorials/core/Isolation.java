import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.blades.misc.Printer;
import org.agilewiki.jactor2.core.plant.BasicPlant;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;

public class Isolation extends BladeBase {
    
    public static void main(final String[] _args) throws Exception {
        BasicPlant plant = new Plant();
        try {
            Printer printer = Printer.stdoutAReq().call();
            
            printer.printlnSReq("\nBump with NonBlockingReactor\n").call();
            Isolation isolation = new Isolation(new NonBlockingReactor(plant), printer);
            isolation.bumpAReq().signal();
            isolation.bumpAReq().signal();
            isolation.bumpAReq().call(); //call forces all pending bump requests to complete
            
            printer.printlnSReq("\nBump with IsolationReactor\n").call();
            isolation = new Isolation(new IsolationReactor(), printer);
            isolation.bumpAReq().signal();
            isolation.bumpAReq().signal();
            isolation.bumpAReq().call(); //call forces all pending bump requests to complete
        } finally {
            plant.close();
        }
    }
    
    final Printer printer;
    int state;
    
    public Isolation(final Reactor _reactor, final Printer _printer) throws Exception {
        _initialize(_reactor);
        printer = _printer;
    }
    
    AsyncRequest<Void> bumpAReq() {
        return new AsyncBladeRequest<Void>() {
            AsyncResponseProcessor dis = this;

            public void processAsyncRequest() throws Exception {
                int oldState = state;
                int newState = state + 1;
                send(printer.printfSReq("was %d, now %d\n", oldState, newState), 
                        new AsyncResponseProcessor<Void>() {
                    public void processAsyncResponse(final Void _response) throws Exception {
                        state = newState; //belated update
                        dis.processAsyncResponse(null);
                    }
                });
            }
        };
    }
}
