package org.agilewiki.pactor.util.durable.app;

import org.agilewiki.pactor.util.durable.Durables;
import org.agilewiki.pactor.util.durable.FactoryLocator;
import org.agilewiki.pactor.util.durable.incDes.PAInteger;
import org.agilewiki.pactor.util.durable.incDes.PAString;

public class User extends AppBase {
    static void register(final FactoryLocator _factoryLocator) throws Exception {
        Durables.registerAppFactory(_factoryLocator, User.class, "user",
                PAString.FACTORY_NAME, PAInteger.FACTORY_NAME, PAString.FACTORY_NAME);
    }

    static int NAME = 0;
    static int AGE = 1;
    static int LOCATION = 2;

    PAString PAName() throws Exception {
        return (PAString) getDurable()._iGet(NAME);
    }

    PAInteger PAAge() throws Exception {
        return (PAInteger) getDurable()._iGet(AGE);
    }

    PAString PALocation() throws Exception {
        return (PAString) getDurable()._iGet(LOCATION);
    }
}
