import org.agilewiki.jactor2.core.blades.misc.Printer;
import org.agilewiki.jactor2.core.plant.BasicPlant;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.messages.SyncRequest;

public class HelloWorld {
    public static void main(final String[] _args) throws Exception {
        BasicPlant plant = new Plant();
        try {
            Printer.printlnAReq("Hello World!").call();
        } finally {
            plant.close();
        }
    }
}
