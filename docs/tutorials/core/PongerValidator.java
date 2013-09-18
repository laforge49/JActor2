import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.messages.SyncRequest;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;

public class PongerValidator {
    public static void main(final String[] _args) throws Exception {
        Facility facility = new Facility();
        try {
            Reactor reactor = new NonBlockingReactor(facility);
            Ponger ponger = new Ponger(reactor);
            long i = 0;
            while (i < 10) {
                i++;
                SyncRequest<Long> ping = ponger.pingSReq();
                long j = ping.call();
                if (i != j)
                    throw new IllegalStateException("unexpected result");
            }
        } finally {
            facility.close();
        }
    }
}
