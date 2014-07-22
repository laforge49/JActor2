import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.requests.SOp;
import org.agilewiki.jactor2.core.requests.impl.RequestImpl;

public class Signals extends NonBlockingBladeBase {

	public Signals() throws Exception {
	}
    
    public static void main(final String[] _args) throws Exception {
        new Plant();
        try {
            Signals signals = new Signals();
            int i = 0;
            while (i < 10) {
                i++;
                signals.printSOp(i).signal();
            }
            signals.getReactor().nullSOp().call();
        } finally {
            Plant.close();
        }
    }
        
    SOp<Void> printSOp(final Integer _i) {
        return new SOp<Void>("print", getReactor()) {
            @Override
            public Void processSyncOperation(final RequestImpl _requestImpl) throws Exception {
                System.out.println(_i);
                return null;
            }
        };
    }
}
