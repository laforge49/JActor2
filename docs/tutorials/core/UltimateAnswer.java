import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.blades.misc.Printer;
import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.SyncRequest;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;

public class UltimateAnswer extends BladeBase {
    private final Printer printer;

    public UltimateAnswer(final Reactor _reactor, final Printer _printer) throws Exception {
        initialize(_reactor);
        printer = _printer;
    }
    
    public AsyncRequest<Void> printAnswerAReq() {
        return new AsyncBladeRequest<Void>() {
            final AsyncResponseProcessor<Void> dis = this;

            @Override
            protected void processAsyncRequest() throws Exception {
                SyncRequest<Void> printRequest = printer.printlnSReq("*** 42 ***");
                send(printRequest, dis);
            }
        };
    }
    
    public static void main(final String[] _args) throws Exception {
        Facility facility = new Facility();
        try {
            Printer printer = new Printer(new IsolationReactor(facility));
            UltimateAnswer ultimateAnswer = new UltimateAnswer(
                new NonBlockingReactor(facility),
                printer);
            AsyncRequest<Void> printAnswerAReq = ultimateAnswer.printAnswerAReq();
            printAnswerAReq.call();
        } finally {
            facility.close();
        }
    }
}