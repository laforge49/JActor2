import org.agilewiki.jactor2.core.blades.*;
import org.agilewiki.jactor2.core.facilities.*;
import org.agilewiki.jactor2.core.messages.*;
import org.agilewiki.jactor2.core.reactors.*;

public class HelloWorld {
    public static void main(final String[] _args) throws Exception {
        Facility facility = new Facility();
        try {
            NonBlockingReactor reactor = new NonBlockingReactor(facility);
            HelloWorldBlade helloWorldBlade = new HelloWorldBlade(reactor);
            AsyncRequest<String> getGreetingAReq = helloWorldBlade.getGreetingAReq();
            String response = getGreetingAReq.call();
            System.out.println(response);
        } finally {
            facility.close();
        }
    }
}