import org.agilewiki.jactor2.core.facilities.Plant;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class PongerValidator {
    public static void main(final String[] _args) throws Exception {
        Plant plant = new Plant();
        try {
            Ponger ponger = new Ponger(new NonBlockingReactor(plant));
            long i = 0;
            while (i < 10) {
                i++;
                long j = ponger.pingSReq().call();
                if (i != j)
                    throw new IllegalStateException("unexpected result");
            }
        } finally {
            plant.close();
        }
    }
}