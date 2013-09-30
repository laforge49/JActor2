import org.agilewiki.jactor2.core.blades.misc.Printer;
import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.messages.SyncRequest;

public class HelloWorld {
    public static void main(final String[] _args) throws Exception {
        Facility facility = new Facility();
        try {
            Printer printer = Printer.stdoutSReq(facility).call();
            SyncRequest<Void> printRequest = printer.printlnSReq("Hello World!");
            printRequest.call();
        } finally {
            facility.close();
        }
    }
}
