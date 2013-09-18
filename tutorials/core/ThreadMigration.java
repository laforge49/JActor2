import org.agilewiki.jactor2.core.blades.*;
import org.agilewiki.jactor2.core.facilities.*;
import org.agilewiki.jactor2.core.messages.*;
import org.agilewiki.jactor2.core.reactors.*;

public class ThreadMigration extends BladeBase {
    public static void main(final String[] _args) 
            throws Exception {
        Facility facility = new Facility();
        try {
            System.out.println("\n           main thread: " + 
                Thread.currentThread());
            Reactor reactor = 
                new NonBlockingReactor(facility);
            ThreadMigration threadMigration = 
                new ThreadMigration(reactor);
            threadMigration.startAReq().call();
        } finally {
            facility.close();
        }
    }
    
    public ThreadMigration(final Reactor _reactor) 
            throws Exception {
        initialize(_reactor);
    }
    
    public AsyncRequest<Void> startAReq() {
        return new AsyncBladeRequest<Void>() {
            @Override
            protected void processAsyncRequest() 
                    throws Exception {
                System.out.println("ThreadMigration thread: " + Thread.currentThread());
                Reactor myReactor = getReactor();
                Facility myModuleContext = myReactor.getFacility();
                Reactor subReactor = 
                    new NonBlockingReactor(myModuleContext);
                SubActor subActor = new SubActor(subReactor);
                subActor.doAReq("         signal").signal();
                send(subActor.doAReq("           send"), this);
            }
        };
    }
}

class SubActor extends BladeBase {
    public SubActor(final Reactor _reactor) 
            throws Exception {
        initialize(_reactor);
    }
    
    public AsyncRequest<Void> doAReq(final String _label) {
        return new AsyncBladeRequest<Void>() {
            @Override
            protected void processAsyncRequest() 
                    throws Exception {
                System.out.println(_label + " thread: " + 
                    Thread.currentThread());
                processAsyncResponse(null);
            }
        };
    }
}
