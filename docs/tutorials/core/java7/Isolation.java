import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.blades.Delay;
import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;

public class Isolation extends NonBlockingBladeBase {
    
    public static void main(final String[] _args) throws Exception {
        new Plant();
        try {
            Isolation isolation = new Isolation();

            System.out.println("\nBump with NonBlockingReactor\n");
            Isolate isolate = new Isolate(new NonBlockingReactor());
            isolation.runAReq(isolate).call();
            
            System.out.println("\nBump with IsolationReactor\n");
            isolate = new Isolate(new IsolationReactor());
            isolation.runAReq(isolate).call();
        } finally {
            Plant.close();
        }
    }
    
    AsyncRequest<Void> runAReq(final Isolate _isolate) {
        return new AsyncBladeRequest<Void>() {
            AsyncRequest<Void> dis = this;
            
            AsyncResponseProcessor<Void> ignoreResponse = new AsyncResponseProcessor<Void>() {
                public void processAsyncResponse(Void response) {
                }
            };
            
            public void processAsyncRequest() {
                send(_isolate.bumpAReq(), ignoreResponse);
                send(_isolate.bumpAReq(), ignoreResponse);
                send(_isolate.bumpAReq(), dis);
            }
        };
    }
}
 
class Isolate extends BladeBase { 
    int state;
    
    Isolate(final Reactor _reactor) {
        _initialize(_reactor);
    }
    
    AsyncRequest<Void> bumpAReq() {
        return new AsyncBladeRequest<Void>() {
            AsyncRequest dis = this;

            public void processAsyncRequest() {
                int oldState = state;
                int newState = state + 1;
                System.out.println("was " + oldState + ", now " + newState); 
                Delay delay = new Delay();
                send(delay.sleepSReq(1), new AsyncResponseProcessor<Void>() {
                    public void processAsyncResponse(Void response) {
                        state = newState; //belated update
                        dis.processAsyncResponse(null);
                    }
                });
            }
        };
    }
}
