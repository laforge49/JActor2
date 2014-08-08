import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.requests.SOp;
import org.agilewiki.jactor2.core.requests.impl.RequestImpl;

class Hanger extends NonBlockingBladeBase {
    Hanger() throws Exception {
        super(new NonBlockingReactor());
    }

    SOp<Void> looperSOp() {
        return new SOp<Void>("looper", getReactor()) {
            @Override
            protected Void processSyncOperation(final RequestImpl _requestImpl) throws Exception {
                while (true) {}
            }
        };
    }

    SOp<Void> sleeperSOp() {
        return new SOp<Void>("sleeper", getReactor()) {
            @Override
            protected Void processSyncOperation(final RequestImpl _requestImpl) throws Exception {
                Thread.sleep(Long.MAX_VALUE);
                return null;
            }
        };
    }
}
