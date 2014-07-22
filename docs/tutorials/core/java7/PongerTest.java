import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class PongerTest {
    public static void main(final String[] _args) throws Exception {
        new Plant();
        try {
            Ponger ponger = new Ponger();
            long j = ponger.pingSOp().call();
            if (1 != j)
                throw new IllegalStateException("unexpected result");
        } finally {
            Plant.close();
        }
    }
}
