import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.requests.SyncRequest;
import org.agilewiki.jactor2.core.requests.StackOverflowException;

public class StackOverflow extends NonBlockingBladeBase {
    StackOverflow() throws Exception {}
    
    void recure() {
        recure();
    }

    SyncRequest<Void> recureSReq() {
        return new SyncBladeRequest() {
            @Override
            public Void processSyncRequest() throws Exception {
                recure();
                return null;
            }
        };
    }
    
    public static void main(final String[] args) throws Exception {
        Plant plant = new Plant();
        try {
            new StackOverflow().recureSReq().call();
        } catch (final StackOverflowException soe) {
            System.out.println("\nCaught "+soe);
        } finally {
            plant.close();
        }
    }
}