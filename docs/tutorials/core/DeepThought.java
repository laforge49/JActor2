import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.blades.misc.Delay;
import org.agilewiki.jactor2.core.blades.misc.Printer;
import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.facilities.Plant;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.SyncRequest;
import org.agilewiki.jactor2.core.reactors.BlockingReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;

public class DeepThought extends BladeBase {
    public DeepThought(final Reactor _reactor) throws Exception {
        initialize(_reactor);
    }
    
    public AsyncRequest<Void> printAnswerAReq() {
        return new AsyncBladeRequest<Void>() {
            final AsyncResponseProcessor<Void> dis = this;

            final AsyncResponseProcessor<Printer> stdoutResponseProcessor =
                    new AsyncResponseProcessor<Printer>() {
                @Override
                public void processAsyncResponse(final Printer _printer) throws Exception {
                    SyncRequest<Void> printRequest = _printer.printlnSReq("I am sorry, but did you say something?");
                    send(printRequest, dis);
                }
            };

            final AsyncResponseProcessor<Void> sleepResponseProcessor =
                    new AsyncResponseProcessor<Void>() {
                @Override
                public void processAsyncResponse(final Void _response) throws Exception {
                    AsyncRequest<Printer> stdoutRequest = Printer.stdoutAReq((Plant) getReactor().getFacility());
                    send(stdoutRequest, stdoutResponseProcessor);
                }
            };

            @Override
            protected void processAsyncRequest() throws Exception {
                Reactor myReactor = getReactor();
                Facility myFacility = myReactor.getFacility();
                Delay delay = new Delay(new BlockingReactor(myFacility));
                SyncRequest<Void> sleepSReq = delay.sleepSReq(4000);
                send(sleepSReq, sleepResponseProcessor);
            }
        };
    }
    
    public static void main(final String[] _args) throws Exception {
        Plant plant = new Plant();
        try {
            DeepThought deepThought = new DeepThought(new NonBlockingReactor(plant));
            AsyncRequest<Void> printAnswerAReq = deepThought.printAnswerAReq();
            printAnswerAReq.call();
        } finally {
            plant.close();
        }
    }
}
