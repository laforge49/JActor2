import org.agilewiki.jactor2.core.blades.oldTransactions.oldProperties.PropertiesBlade;
import org.agilewiki.jactor2.core.facilities.Plant;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class TransactionSpeedReport extends PropertiesBlade {
    final long count = 5000000L;

    public static void main(final String[] _args) throws Exception {
        Plant plant = new Plant();
        try {
            TransactionSpeedReport transactionSpeedReport = 
                new TransactionSpeedReport(new NonBlockingReactor(plant));
            transactionSpeedReport.goAReq().call();
        } finally {
            plant.close();
        }
    }

    TransactionSpeedReport(final NonBlockingReactor _reactor) throws Exception {
        super(_reactor);
    }
    
    AsyncRequest<Void> goAReq() {
        return new AsyncBladeRequest<Void>() {
            final AsyncResponseProcessor<Void> dis = this;
            long i;
            long before;
            
            final AsyncResponseProcessor<Void> putResponseProcessor =
                    new AsyncResponseProcessor<Void>() {
                @Override
                public void processAsyncResponse(final Void _response) throws Exception {
                    i++;
                    if (i == count) {
                        final long after = System.nanoTime();
                        final long duration = after - before;
                        send(SpeedReport.startAReq(getReactor().getFacility(), 
                            "Transaction Timings", duration, count), dis);
                        return;
                    }
                    send(putAReq("1", i), putResponseProcessor);
                }
            };

            @Override
            protected void processAsyncRequest() throws Exception {
                before = System.nanoTime();
                send(putAReq("1", i), putResponseProcessor);
            }
        };
    }
}
