import org.agilewiki.jactor2.core.*;
import org.agilewiki.jactor2.core.threading.*;
import org.agilewiki.jactor2.core.messaging.*;
import org.agilewiki.jactor2.core.processing.*;

public class HelloWorldActor extends ActorBase {

    public HelloWorldActor(final MessageProcessor _messageProcessor) throws Exception {
        initialize(_messageProcessor);
    }
    
    public AsyncRequest<String> getGreetingAReq() {
        return new AsyncRequest<String>(getMessageProcessor()) {
            @Override
            public void processAsyncRequest() throws Exception {
                processAsyncResponse("Hello world!");
            }
        };
    }
}
