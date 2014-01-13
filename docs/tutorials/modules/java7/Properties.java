import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

import org.agilewiki.jactor2.modules.MPlant;
import org.agilewiki.jactor2.modules.immutable.ImmutableProperties;
import org.agilewiki.jactor2.modules.transactions.properties.PropertiesChangeManager;
import org.agilewiki.jactor2.modules.transactions.properties.PropertiesProcessor;
import org.agilewiki.jactor2.modules.transactions.properties.PropertiesTransactionAReq;

import java.util.*;

public class Properties {
    public static void main(final String[] _args) throws Exception {
        new MPlant();
        try {
            PropertiesProcessor propertiesProcessor = 
                new PropertiesProcessor(new NonBlockingReactor());
            propertiesProcessor.putAReq("a", 1).call();
            System.out.println(propertiesProcessor.getImmutableState());
            new IncAReq(propertiesProcessor, "a", 41).call();
            System.out.println(propertiesProcessor.getImmutableState());
        } finally {
            Plant.close();
        }
    }
}

class IncAReq extends PropertiesTransactionAReq {
    final String name;
    final int increment;

    public IncAReq(final PropertiesProcessor _propertiesProcessor,
                   final String _name,
                   final int _increment) {
        super(_propertiesProcessor.parentReactor, _propertiesProcessor);
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
