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
            threadMigration.startReq().call();
        } finally {
            moduleContext.close();
        }
    }
    
    public ThreadMigration(final MessageProcessor _messageProcessor) 
            throws Exception {
        initialize(_messageProcessor);
    }
    
    public Request<Void> startReq() {
        return new Request<Void>(getMessageProcessor()) {
            @Override
            public void processRequest(final Transport<Void> _transport) 
                    throws Exception {
                System.out.println("ThreadMigration thread: " + Thread.currentThread());
                MessageProcessor myMessageProcessor = getMessageProcessor();
                ModuleContext myModuleContext = myMessageProcessor.getModuleContext();
                MessageProcessor subMessageProcessor = 
                    new NonBlockingMessageProcessor(myModuleContext);
                SubActor subActor = new SubActor(subMessageProcessor);
                subActor.doReq("         signal").signal();
                subActor.doReq("           send").send(myMessageProcessor, _transport);
            }
        };
    }
}

class SubActor extends ActorBase {
    public SubActor(final MessageProcessor _messageProcessor) 
            throws Exception {
        initialize(_messageProcessor);
    }
    
    public Request<Void> doReq(final String _label) {
        return new Request<Void>(getMessageProcessor()) {
            @Override
            public void processRequest(final Transport<Void> _transport) 
                    throws Exception {
                System.out.println(_label + " thread: " + 
                    Thread.currentThread());
                _transport.processResponse(null);
            }
        };
    }
}
