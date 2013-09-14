import org.agilewiki.jactor2.core.blades.*;
import org.agilewiki.jactor2.core.facilities.*;
import org.agilewiki.jactor2.core.messages.*;
import org.agilewiki.jactor2.core.reactors.*;

public class HelloWorldBlade extends BladeBase {

    public HelloWorldBlade(final Reactor _reactor) throws Exception {
        initialize(_reactor);
    }
    
    public AsyncRequest<String> getGreetingAReq() {
        return new AsyncRequest<String>(getReactor()) {
            @Override
            public void processAsyncRequest() throws Exception {
                processAsyncResponse("Hello world!");
            }
        };
    }
}
