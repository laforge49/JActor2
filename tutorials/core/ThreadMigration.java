import org.agilewiki.jactor2.core.*;
import org.agilewiki.jactor2.core.threading.*;
import org.agilewiki.jactor2.core.messaging.*;
import org.agilewiki.jactor2.core.processing.*;

public class ThreadMigration extends ActorBase {
    public static void main(final String[] _args) 
            throws Exception {
        ModuleContext moduleContext = new ModuleContext();
        try {
            System.out.println("\n           main thread: " + 
                Thread.currentThread());
            MessageProcessor messageProcessor = 
                new NonBlockingMessageProcessor(moduleContext);
            ThreadMigration threadMigration = 
                new ThreadMigration(messageProcessor);
            threadMigration.startAReq().call();
        } finally {
            moduleContext.close();
        }
    }
    
    public ThreadMigration(final MessageProcessor _messageProcessor) 
            throws Exception {
        initialize(_messageProcessor);
    }
    
    public AsyncRequest<Void> startAReq() {
        return new AsyncRequest<Void>(getMessageProcessor()) {
            @Override
            public void processAsyncRequest() 
                    throws Exception {
                System.out.println("ThreadMigration thread: " + Thread.currentThread());
                MessageProcessor myMessageProcessor = getMessageProcessor();
                ModuleContext myModuleContext = myMessageProcessor.getModuleContext();
                MessageProcessor subMessageProcessor = 
                    new NonBlockingMessageProcessor(myModuleContext);
                SubActor subActor = new SubActor(subMessageProcessor);
                subActor.doAReq("         signal").signal();
                subActor.doAReq("           send").send(myMessageProcessor, this);
            }
        };
    }
}

class SubActor extends ActorBase {
    public SubActor(final MessageProcessor _messageProcessor) 
            throws Exception {
        initialize(_messageProcessor);
    }
    
    public AsyncRequest<Void> doAReq(final String _label) {
        return new AsyncRequest<Void>(getMessageProcessor()) {
            @Override
            public void processAsyncRequest() 
                    throws Exception {
                System.out.println(_label + " thread: " + 
                    Thread.currentThread());
                processAsyncResponse(null);
            }
        };
    }
}
