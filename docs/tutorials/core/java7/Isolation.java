import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.blades.misc.Delay;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;

public class Isolation extends BladeBase {
    
    public static void main(final String[] _args) throws Exception {
        new Plant();
        try {
            System.out.println("\nBump with NonBlockingReactor\n");
            Isolation isolation = new Isolation(new NonBlockingReactor());
            isolation.bumpAReq().signal();
            isolation.bumpAReq().signal();
            isolation.bumpAReq().call(); //call forces all pending bump requests to complete
            
            System.out.println("\nBump with IsolationReactor\n");
            isolation = new Isolation(new IsolationReactor());
            isolation.bumpAReq().signal();
            isolation.bumpAReq().signal();
            isolation.bumpAReq().call(); //call forces all pending bump requests to complete
        } finally {
            Plant.close();
        }
    }
    
    int state;
    
    public Isolation(final Reactor _reactor) throws Exception {
        _initialize(_reactor);
    }
    
    AsyncRequest<Void> bumpAReq() {
        return new AsyncBladeRequest<Void>() {
            AsyncResponseProcessor dis = this;

            public void processAsyncRequest() throws Exception {
                int oldState = state;
                int newState = state + 1;
                System.out.println("was " + oldState + ", now " + newState); 
                Delay delay = new Delay();
                send(delay.sleepSReq(1), new AsyncResponseProcessor<Void>() {
                    public void processAsyncResponse(Void response) throws Exception {
                        state = newState; //belated update
                        dis.processAsyncResponse(null);
                    }
                });
            }
        };
    }
}
