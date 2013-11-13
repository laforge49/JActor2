import org.agilewiki.jactor2.core.blades.transactions.properties.PropertiesChangeManager;
import org.agilewiki.jactor2.core.blades.transactions.properties.PropertiesProcessor;
import org.agilewiki.jactor2.core.blades.transactions.properties.PropertiesTransactionAReq;
import org.agilewiki.jactor2.core.facilities.Plant;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.util.immutable.ImmutableProperties;

import java.util.*;

public class Properties {
    public static void main(final String[] _args) throws Exception {
        final Plant plant = new Plant();
        try {
            PropertiesProcessor propertiesProcessor = 
                new PropertiesProcessor(new IsolationReactor(plant));
            propertiesProcessor.putAReq("a", 1).call();
            printIt(propertiesProcessor);
            new IncAReq(propertiesProcessor, "a", 41).call();
            printIt(propertiesProcessor);
        } finally {
            plant.close();
        }
    }
    
    static void printIt(final PropertiesProcessor _propertiesProcessor) {
        ImmutableProperties<Object> immutableProperties = _propertiesProcessor.getImmutableState();
        System.out.println("\n");
        Set<Map.Entry<String, Object>> entries = immutableProperties.entrySet();
        System.out.println(entries);
    }
}

class IncAReq extends PropertiesTransactionAReq {
    final String name;
    final int increment;

    public IncAReq(final PropertiesProcessor _propertiesProcessor,
                   final String _name,
                   final int _increment) {
        super(_propertiesProcessor.commonReactor, _propertiesProcessor);
        name = _name;
        increment = _increment;
    }

    protected void update(final PropertiesChangeManager _contentManager) throws Exception {
        int oldValue = (Integer) _contentManager.getImmutableProperties().get(name);
        int newValue = oldValue + increment;
        _contentManager.put(name, newValue);
    }
}
