import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.blades.misc.Delay;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.SyncRequest;
import org.agilewiki.jactor2.core.reactors.BlockingReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class DeepThought extends NonBlockingBladeBase {
    public DeepThought(final NonBlockingReactor _reactor) throws Exception {
        initialize(_reactor);
    }
    
    public AsyncRequest<Void> printAnswerAReq() {
        return new AsyncBladeRequest<Void>() {
            final AsyncResponseProcessor<Void> dis = this;

            final AsyncResponseProcessor<Void> sleepResponseProcessor =
                    new AsyncResponseProcessor<Void>() {
                @Override
                public void processAsyncResponse(final Void _response) throws Exception {
                    System.out.println("I am sorry, but did you say something?");
                    dis.processAsyncResponse(null);
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
        Plant plant = new Plant();
        try {
            DeepThought deepThought = new DeepThought(new NonBlockingReactor());
            AsyncRequest<Void> printAnswerAReq = deepThought.printAnswerAReq();
            printAnswerAReq.call();
        } finally {
            plant.close();
        }
    }
}
