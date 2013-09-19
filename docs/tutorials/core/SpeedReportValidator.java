import org.agilewiki.jactor2.core.blades.misc.Printer;
import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;

public class SpeedReportValidator {
    public static void main(final String[] _args) throws Exception {
        Facility facility = new Facility();
        try {
            Printer printer = new Printer(new IsolationReactor(facility));
            SpeedReport.startAReq(printer, "Speed Report Validation", 1000L, 1L).call();
        } finally {
            facility.close();
        }
    }
}
