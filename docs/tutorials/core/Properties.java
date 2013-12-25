import org.agilewiki.jactor2.core.blades.transactions.properties.PropertiesChangeManager;
import org.agilewiki.jactor2.core.blades.transactions.properties.PropertiesProcessor;
import org.agilewiki.jactor2.core.blades.transactions.properties.PropertiesTransactionAReq;
import org.agilewiki.jactor2.core.plant.BasicPlant;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.util.immutable.ImmutableProperties;

import java.util.*;

public class Properties {
    public static void main(final String[] _args) throws Exception {
        final BasicPlant plant = new Plant();
        try {
            PropertiesProcessor propertiesProcessor = 
                new PropertiesProcessor(new IsolationReactor(plant));
            propertiesProcessor.putAReq("a", 1).call();
            System.out.println(propertiesProcessor.getImmutableState());
            new IncAReq(propertiesProcessor, "a", 41).call();
            System.out.println(propertiesProcessor.getImmutableState());
        } finally {
            plant.close();
        }
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

    @Override
    protected void update(final PropertiesChangeManager _contentManager) throws Exception {
        int oldValue = (Integer) _contentManager.getImmutableProperties().get(name);
        int newValue = oldValue + increment;
        _contentManager.put(name, newValue);
    }
}
