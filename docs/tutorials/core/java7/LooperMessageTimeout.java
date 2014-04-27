import org.agilewiki.jactor2.core.impl.Plant;

public class LooperMessageTimeout {
    static public void main(final String[] _args) throws Exception {
        final Plant plant = new Plant();
        try {
            Hanger hanger = new Hanger();
            hanger.looperSReq().signal();
            Thread.sleep(Long.MAX_VALUE);
        } finally {
            plant.close();
        }
    }
}
