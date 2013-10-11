import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.facilities.Plant;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;

public class Greeter extends BladeBase {
    public Greeter(final Reactor _reactor) throws Exception {
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
        Plant plant = new Plant();
        try {
            Greeter greeter = new Greeter(new NonBlockingReactor(plant));
            AsyncRequest<String> greetingAReq = greeter.greetingAReq("Joe");
            String greeting = greetingAReq.call();
            if (!"Hi Joe".equals(greeting))
                throw new IllegalStateException("invalid response: " + greeting);
        } finally {
            plant.close();
        }
    }
}