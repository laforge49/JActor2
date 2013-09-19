import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class PongerValidator {
    public static void main(final String[] _args) throws Exception {
        Facility facility = new Facility();
        try {
            Ponger ponger = new Ponger(new NonBlockingReactor(facility));
            long i = 0;
            while (i < 10) {
                i++;
                long j = ponger.pingSReq().call();
                if (i != j)
                    throw new IllegalStateException("unexpected result");
            }
        } finally {
            facility.close();
        }
    }
}
