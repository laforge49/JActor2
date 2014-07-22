import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.impl.Delay;
import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.requests.AOp;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.requests.impl.AsyncRequestImpl;

public class Isolation extends NonBlockingBladeBase {
    
    public static void main(final String[] _args) throws Exception {
        new Plant();
        try {
            Isolation isolation = new Isolation();

            System.out.println("\nBump with NonBlockingReactor\n");
            Isolate isolate = new Isolate(new NonBlockingReactor());
            isolation.runAOp(isolate).call();
            
            System.out.println("\nBump with IsolationReactor\n");
            isolate = new Isolate(new IsolationReactor());
            isolation.runAOp(isolate).call();
        } finally {
            Plant.close();
        }
    }
	
	public Isolation() throws Exception {
	}
    
    AOp<Void> runAOp(final Isolate _isolate) {
        return new AOp<Void>("run", getReactor()) {
            public void processAsyncOperation(final AsyncRequestImpl _asyncRequestImpl, 
					final AsyncResponseProcessor<Void> _asyncResponseProcessor) throws Exception {
                _asyncRequestImpl.send(_isolate.bumpAOp(), null);
                _asyncRequestImpl.send(_isolate.bumpAOp(), null);
                _asyncRequestImpl.send(_isolate.bumpAOp(), _asyncResponseProcessor);
            }
        };
    }
}
 
class Isolate extends BladeBase { 
    int state;
    
    Isolate(final Reactor _reactor) {
        _initialize(_reactor);
    }
    
    AOp<Void> bumpAOp() {
        return new AOp<Void>("bump", getReactor()) {
            public void processAsyncOperation(final AsyncRequestImpl _asyncRequestImpl, 
					final AsyncResponseProcessor<Void> _asyncResponseProcessor) throws Exception {
                int oldState = state;
                final int newState = state + 1;
                System.out.println("was " + oldState + ", now " + newState); 
                Delay delay = new Delay();
                _asyncRequestImpl.send(delay.sleepSOp(1), new AsyncResponseProcessor<Void>() {
                    public void processAsyncResponse(Void response) throws Exception {
                        state = newState; //belated update
                        _asyncResponseProcessor.processAsyncResponse(null);
                    }
                });
            }
        };
    }
}
