import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.requests.SOp;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.requests.impl.RequestImpl;

public class Ponger extends BladeBase {
    private long count = 0;
    
    public Ponger() throws Exception {
        _initialize(new NonBlockingReactor());
    }
    
    public Ponger(final Reactor _reactor) {
        _initialize(_reactor);
    }

    public SOp<Long> pingSOp() {
        return new SOp("ping", getReactor()) {
            @Override
            protected Long processSyncOperation(final RequestImpl _requestImpl) {
				count += 1;
				return count;
            }
        };
    }
}
