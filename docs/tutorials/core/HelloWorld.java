import org.agilewiki.jactor2.core.blades.misc.Printer;
import org.agilewiki.jactor2.core.facilities.Plant;
import org.agilewiki.jactor2.core.messages.SyncRequest;

public class HelloWorld {
    public static void main(final String[] _args) throws Exception {
        Plant plant = new Plant();
        try {
            Printer.printlnAReq(plant, "Hello World!").call();
        } finally {
            plant.close();
        }
    }
}
