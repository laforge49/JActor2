import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.blades.misc.Delay;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.SyncRequest;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class Echo extends NonBlockingBladeBase {
    public Echo(final NonBlockingReactor _reactor) throws Exception {
        initialize(_reactor);
    }
    
    public AsyncRequest<String> echoAReq(final String text) {
        return new AsyncBladeRequest<String>() {
            final AsyncResponseProcessor<String> dis = this;

            final AsyncResponseProcessor<Void> sleepResponseProcessor =
                    new AsyncResponseProcessor<Void>() {
                @Override
                public void processAsyncResponse(final Void _response) throws Exception {
                    dis.processAsyncResponse(text);
                }
            };

            @Override
            public void processAsyncRequest() throws Exception {
                Delay delay = new Delay();
                SyncRequest<Void> sleepSReq = delay.sleepSReq(2000);
                send(sleepSReq, sleepResponseProcessor);
            }
        };
    }
    
    public static void main(final String[] _args) throws Exception {
        Plant plant = new Plant();
        try {
            Echo echo = new Echo(new NonBlockingReactor());
            AsyncRequest<String> echoAReq = echo.echoAReq("Hello...");
            String response = echoAReq.call();
            if (!"Hello...".equals(response))
                throw new IllegalStateException("invalid response: " + response);
        } finally {
            plant.close();
        }
    }
}