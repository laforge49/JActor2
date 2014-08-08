import org.agilewiki.jactor2.core.blades.*;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.requests.*;
import org.agilewiki.jactor2.core.reactors.*;
import org.agilewiki.jactor2.core.requests.impl.AsyncRequestImpl;

public class ThreadMigration extends NonBlockingBladeBase {
    public static void main(final String[] _args) 
            throws Exception {
        Plant plant = new Plant();
        try {
            System.out.println("\n           main thread: " + 
                Thread.currentThread());
            NonBlockingReactor reactor = 
                new NonBlockingReactor();
            ThreadMigration threadMigration = 
                new ThreadMigration(reactor);
            threadMigration.startAOp().call();
        } finally {
            plant.close();
        }
    }
    
    public ThreadMigration(final NonBlockingReactor _reactor) 
            throws Exception {
        super(_reactor);
    }
    
    public AOp<Void> startAOp() {
        return new AOp<Void>("start", getReactor()) {
            @Override
			protected void processAsyncOperation(final AsyncRequestImpl _asyncRequestImpl, 
					final AsyncResponseProcessor<Void> _asyncResponseProcessor) throws Exception {
                System.out.println("ThreadMigration thread: " + Thread.currentThread());
                NonBlockingReactor subReactor = new NonBlockingReactor();
                SubActor subActor = new SubActor(subReactor);
                subActor.doAOp("         signal").signal();
                _asyncRequestImpl.send(subActor.doAOp("           send"), _asyncResponseProcessor);
            }
        };
    }
}

class SubActor extends NonBlockingBladeBase {
    public SubActor(final NonBlockingReactor _reactor) 
            throws Exception {
        super(_reactor);
    }
    
    public AOp<Void> doAOp(final String _label) {
        return new AOp<Void>("do", getReactor()) {
            @Override
			protected void processAsyncOperation(final AsyncRequestImpl _asyncRequestImpl, 
					final AsyncResponseProcessor<Void> _asyncResponseProcessor) throws Exception {
                System.out.println(_label + " thread: " + 
                    Thread.currentThread());
                _asyncResponseProcessor.processAsyncResponse(null);
            }
        };
    }
}
