import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.reactors.ReactorClosedException;
import org.agilewiki.jactor2.core.requests.SOp;
import org.agilewiki.jactor2.core.requests.impl.RequestImpl;

public class StackOverflow extends NonBlockingBladeBase {

	public StackOverflow() throws Exception {
	}
    
    void recur() {
        recur();
    }

    SOp<Void> recurSOp() {
        return new SOp("recur", getReactor()) {
            @Override
            protected Void processSyncOperation(final RequestImpl _requestImpl) throws Exception {
                recur();
                return null;
            }
        };
    }
    
    public static void main(final String[] args) throws Exception {
        Plant plant = new Plant();
        try {
            new StackOverflow().recurSOp().call();
        } catch (final ReactorClosedException _rce) {
            System.out.println("\nCaught " + _rce + "\n");
        } finally {
            plant.close();
        }
    }
}