import org.agilewiki.jactor2.core.*;
import org.agilewiki.jactor2.core.threading.*;
import org.agilewiki.jactor2.core.messaging.*;
import org.agilewiki.jactor2.core.processing.*;

public class HelloWorld {
    public static void main(final String[] _args) throws Exception {
        ModuleContext context = new ModuleContext();
        try {
            NonBlockingMessageProcessor messageProcessor = new NonBlockingMessageProcessor(context);
            HelloWorldActor helloWorldActor = new HelloWorldActor(messageProcessor);
            Request<String> getGreetingReq = helloWorldActor.getGreetingReq();
            String response = getGreetingReq.call();
            System.out.println(response);
        } finally {
            context.close();
        }
    }
}