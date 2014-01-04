import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class Greeter extends NonBlockingBladeBase {
    public Greeter(final NonBlockingReactor _reactor) throws Exception {
        super(_reactor);
    }
    
    public AsyncRequest<String> greetingAReq(final String _name) {
        return new AsyncBladeRequest<String>() {
            @Override
            public void processAsyncRequest() throws Exception {
                processAsyncResponse("Hi " + _name);
            }
        };
    }
    
    public static void main(final String[] _args) throws Exception {
        Plant plant = new Plant();
        try {
            Greeter greeter = new Greeter(new NonBlockingReactor());
            AsyncRequest<String> greetingAReq = greeter.greetingAReq("Joe");
            String greeting = greetingAReq.call();
            if (!"Hi Joe".equals(greeting))
                throw new IllegalStateException("invalid response: " + greeting);
        } finally {
            plant.close();
        }
    }
}