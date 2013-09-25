import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.blades.misc.Delay;
import org.agilewiki.jactor2.core.blades.misc.Printer;
import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.SyncRequest;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;

public class DeepThought extends BladeBase {
    private final Printer printer;

    public DeepThought(final Reactor _reactor, final Printer _printer) throws Exception {
        initialize(_reactor);
        printer = _printer;
    }
    
    public AsyncRequest<Void> printAnswerAReq() {
        return new AsyncBladeRequest<Void>() {
            final AsyncResponseProcessor<Void> dis = this;

            final AsyncResponseProcessor<Void> sleepResponseProcessor =
                    new AsyncResponseProcessor<Void>() {
                @Override
                public void processAsyncResponse(final Void _response) throws Exception {
                    SyncRequest<Void> printRequest = printer.printlnSReq("I am sorry, but did you say something?");
                    send(printRequest, dis);
                }
            };

            @Override
            protected void processAsyncRequest() throws Exception {
                Reactor myReactor = getReactor();
                Facility myFacility = myReactor.getFacility();
                Delay delay = new Delay(new IsolationReactor(myFacility));
                SyncRequest<Void> sleepSReq = delay.sleepSReq(4000);
                send(sleepSReq, sleepResponseProcessor);
            }
        };
    }
    
    public static void main(final String[] _args) throws Exception {
        Facility facility = new Facility();
        try {
            Printer printer = new Printer(new IsolationReactor(facility));
            DeepThought deepThought = new DeepThought(
                new NonBlockingReactor(facility),
                printer);
            AsyncRequest<Void> printAnswerAReq = deepThought.printAnswerAReq();
            printAnswerAReq.call();
        } finally {
            facility.close();
        }
    }
}
