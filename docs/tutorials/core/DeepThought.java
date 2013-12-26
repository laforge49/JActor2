import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.blades.misc.Delay;
import org.agilewiki.jactor2.core.blades.misc.Printer;
import org.agilewiki.jactor2.core.plant.BasicPlant;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.SyncRequest;
import org.agilewiki.jactor2.core.reactors.BlockingReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class DeepThought extends NonBlockingBladeBase {
    public DeepThought(final NonBlockingReactor _reactor) throws Exception {
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
                    AsyncRequest<Printer> stdoutRequest = Printer.stdoutAReq(getReactor().getPlant());
                    send(stdoutRequest, stdoutResponseProcessor);
                }
            };

            @Override
            public void processAsyncRequest() throws Exception {
                Delay delay = new Delay();
                SyncRequest<Void> sleepSReq = delay.sleepSReq(4000);
                send(sleepSReq, sleepResponseProcessor);
            }
        };
    }
    
    public static void main(final String[] _args) throws Exception {
        BasicPlant plant = new Plant();
        try {
            DeepThought deepThought = new DeepThought(new NonBlockingReactor(plant));
            AsyncRequest<Void> printAnswerAReq = deepThought.printAnswerAReq();
            printAnswerAReq.call();
        } finally {
            plant.close();
        }
    }
}
