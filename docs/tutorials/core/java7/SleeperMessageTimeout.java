import org.agilewiki.jactor2.core.impl.Plant;

public class SleeperMessageTimeout {
    static public void main(final String[] _args) throws Exception {
        final Plant plant = new Plant();
        try {
            Hanger hanger = new Hanger();
            hanger.sleeperSOp().call();
            System.out.println("never gets here");
        } finally {
            plant.close();
        }
    }
}
