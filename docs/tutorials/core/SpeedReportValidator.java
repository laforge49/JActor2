import org.agilewiki.jactor2.core.blades.misc.Printer;
import org.agilewiki.jactor2.core.plant.BasicPlant;
import org.agilewiki.jactor2.core.plant.Plant;

public class SpeedReportValidator {
    public static void main(final String[] _args) throws Exception {
        BasicPlant plant = new Plant();
        try {
            SpeedReport.startAReq(plant, "Speed Report Validation", 1000L, 1L).call();
        } finally {
            plant.close();
        }
    }
}
