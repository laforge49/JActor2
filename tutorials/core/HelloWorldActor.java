import org.agilewiki.jactor2.core.*;
import org.agilewiki.jactor2.core.threading.*;
import org.agilewiki.jactor2.core.messaging.*;
import org.agilewiki.jactor2.core.processing.*;

public class HelloWorldActor extends ActorBase {

    public HelloWorldActor(final MessageProcessor _messageProcessor) throws Exception {
        initialize(_messageProcessor);
    }
    
    public Request<String> getGreetingReq() {
        return new Request<String>(getMessageProcessor()) {
            @Override
            public void processRequest(final Transport<String> _transport) throws Exception {
                _transport.processResponse("Hello world!");
            }
        };
    }
}
