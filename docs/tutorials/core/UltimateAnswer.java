import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.blades.misc.Printer;
import org.agilewiki.jactor2.core.plant.BasicPlant;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class UltimateAnswer extends NonBlockingBladeBase {

    public UltimateAnswer(final NonBlockingReactor _reactor) throws Exception {
        initialize(_reactor);
    }
    
    public AsyncRequest<Void> printAnswerAReq() {
        return new AsyncBladeRequest<Void>() {
            final AsyncResponseProcessor<Void> dis = this;

            @Override
            public void processAsyncRequest() throws Exception {
                AsyncRequest<Void> printRequest = Printer.printlnAReq("*** 42 ***");
                send(printRequest, dis);
            }
        };
    }
    
    public static void main(final String[] _args) throws Exception {
        BasicPlant plant = new Plant();
        try {
            UltimateAnswer ultimateAnswer = new UltimateAnswer(new NonBlockingReactor(plant));
            AsyncRequest<Void> printAnswerAReq = ultimateAnswer.printAnswerAReq();
            printAnswerAReq.call();
        } finally {
            plant.close();
        }
    }
}