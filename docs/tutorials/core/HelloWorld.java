import org.agilewiki.jactor2.core.blades.misc.Printer;
import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.messages.SyncRequest;

public class HelloWorld {
    public static void main(final String[] _args) throws Exception {
        Facility facility = new Facility();
        try {
            Printer.printlnAReq(facility, "Hello World!").call();
        } finally {
            facility.close();
        }
    }
}
