import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.blades.misc.Printer;
import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.facilities.Plant;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;

public class UltimateAnswer extends BladeBase {

    public UltimateAnswer(final Reactor _reactor) throws Exception {
        initialize(_reactor);
    }
    
    public AsyncRequest<Void> printAnswerAReq() {
        return new AsyncBladeRequest<Void>() {
            final AsyncResponseProcessor<Void> dis = this;

            @Override
            protected void processAsyncRequest() throws Exception {
                Facility myFacility = getReactor().getFacility();
                AsyncRequest<Void> printRequest = Printer.printlnAReq(myFacility, "*** 42 ***");
                send(printRequest, dis);
            }
        };
    }
    
    public static void main(final String[] _args) throws Exception {
        Plant plant = new Plant();
        try {
            UltimateAnswer ultimateAnswer = new UltimateAnswer(new NonBlockingReactor(plant));
            AsyncRequest<Void> printAnswerAReq = ultimateAnswer.printAnswerAReq();
            printAnswerAReq.call();
        } finally {
            plant.close();
        }
    }
}