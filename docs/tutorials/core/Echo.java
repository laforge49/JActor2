import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.blades.misc.Delay;
import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.SyncRequest;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;

public class Echo extends BladeBase {
    public Echo(final Reactor _reactor) throws Exception {
        initialize(_reactor);
    }
    
    public AsyncRequest<String> echoAReq(final String text) {
        return new AsyncBladeRequest<String>() {
            final AsyncResponseProcessor<String> dis = this;

            final AsyncResponseProcessor<Void> sleepResponseProcessor = new AsyncResponseProcessor<Void>() {
                @Override
                public void processAsyncResponse(final Void _response) throws Exception {
                    dis.processAsyncResponse(text);
                }
            };

            @Override
            protected void processAsyncRequest() throws Exception {
                Reactor myReactor = getReactor();
                Facility myFacility = myReactor.getFacility();
                Delay delay = new Delay(new IsolationReactor(myFacility));
                SyncRequest<Void> sleepSReq = delay.sleepSReq(2000);
                send(sleepSReq, sleepResponseProcessor);
            }
        };
    }
    
    public static void main(final String[] _args) throws Exception {
        Facility facility = new Facility();
        try {
            Echo echo = new Echo(new NonBlockingReactor(facility));
            AsyncRequest<String> echoAReq = echo.echoAReq("Hello...");
            String response = echoAReq.call();
            if (!"Hello...".equals(response))
                throw new IllegalStateException("invalid response: " + response);
        } finally {
            facility.close();
        }
    }
}