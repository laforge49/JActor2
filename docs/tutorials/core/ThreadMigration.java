import org.agilewiki.jactor2.core.blades.*;
import org.agilewiki.jactor2.core.facilities.*;
import org.agilewiki.jactor2.core.plant.*;
import org.agilewiki.jactor2.core.messages.*;
import org.agilewiki.jactor2.core.reactors.*;

public class ThreadMigration extends NonBlockingBladeBase {
    public static void main(final String[] _args) 
            throws Exception {
        Plant plant = new Plant();
        try {
            System.out.println("\n           main thread: " + 
                Thread.currentThread());
            NonBlockingReactor reactor = 
                new NonBlockingReactor(plant);
            ThreadMigration threadMigration = 
                new ThreadMigration(reactor);
            threadMigration.startAReq().call();
        } finally {
            plant.close();
        }
    }
    
    public ThreadMigration(final NonBlockingReactor _reactor) 
            throws Exception {
        initialize(_reactor);
    }
    
    public AsyncRequest<Void> startAReq() {
        return new AsyncBladeRequest<Void>() {
            @Override
            protected void processAsyncRequest() 
                    throws Exception {
                System.out.println("ThreadMigration thread: " + Thread.currentThread());
                NonBlockingReactor myReactor = getReactor();
                Facility myModuleContext = myReactor.getFacility();
                NonBlockingReactor subReactor = 
                    new NonBlockingReactor(myModuleContext);
                SubActor subActor = new SubActor(subReactor);
                subActor.doAReq("         signal").signal();
                send(subActor.doAReq("           send"), this);
            }
        };
    }
}

class SubActor extends NonBlockingBladeBase {
    public SubActor(final NonBlockingReactor _reactor) 
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
