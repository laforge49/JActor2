package org.agilewiki.jactor2.util.durable.app;

import org.agilewiki.jactor2.util.durable.Durables;
import org.agilewiki.jactor2.util.durable.FactoryLocator;
import org.agilewiki.jactor2.util.durable.incDes.JAInteger;
import org.agilewiki.jactor2.util.durable.incDes.JAString;

public class User extends AppBase {
    static void register(final FactoryLocator _factoryLocator) throws Exception {
        Durables.registerAppFactory(_factoryLocator, User.class, "user",
                JAString.FACTORY_NAME, JAInteger.FACTORY_NAME, JAString.FACTORY_NAME);
    }

    static int NAME = 0;
    static int AGE = 1;
    static int LOCATION = 2;

    JAString PAName() throws Exception {
        return (JAString) getDurable()._iGet(NAME);
    }

    JAInteger PAAge() throws Exception {
        return (JAInteger) getDurable()._iGet(AGE);
    }

    JAString PALocation() throws Exception {
        return (JAString) getDurable()._iGet(LOCATION);
    }
}
