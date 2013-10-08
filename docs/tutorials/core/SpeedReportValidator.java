import org.agilewiki.jactor2.core.blades.misc.Printer;
import org.agilewiki.jactor2.core.facilities.Facility;

public class SpeedReportValidator {
    public static void main(final String[] _args) throws Exception {
        Facility facility = new Facility();
        try {
            SpeedReport.startAReq(facility, "Speed Report Validation", 1000L, 1L).call();
        } finally {
            facility.close();
        }
    }
}
