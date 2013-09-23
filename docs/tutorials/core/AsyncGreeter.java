import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.blades.misc.Printer;
import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.SyncRequest;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;

public class AsyncGreeter extends BladeBase {
    public AsyncGreeter(final Reactor _reactor) throws Exception {
        initialize(_reactor);
    }
    
    public AsyncRequest<String> greetingAReq(final String _name) {
        return new AsyncBladeRequest<String>() {
            @Override
            protected void processAsyncRequest() throws Exception {
                processAsyncResponse("Hi " + _name);
            }
        };
    }
    
    public static void main(final String[] _args) throws Exception {
        Facility facility = new Facility();
        try {
            AsyncGreeter asyncGreeter = new AsyncGreeter(new NonBlockingReactor(facility));
            AsyncRequest<String> greetingAReq = asyncGreeter.greetingAReq("Joe");
            String greeting = greetingAReq.call();
            if (!"Hi Joe".equals(greeting))
                throw new IllegalStateException("invalid response: " + greeting);
        } finally {
            facility.close();
        }
    }
}