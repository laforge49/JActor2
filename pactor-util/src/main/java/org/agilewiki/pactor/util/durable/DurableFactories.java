package org.agilewiki.pactor.util.durable;

import org.agilewiki.pactor.api.MailboxFactory;
import org.agilewiki.pactor.utilImpl.durable.IncDesFactory;
import org.agilewiki.pactor.utilImpl.durable.scalar.flens.*;
import org.agilewiki.pactor.utilImpl.durable.scalar.vlens.*;

public class DurableFactories {
    public static MailboxFactory createMailboxFactory() throws Exception {
        MailboxFactory mailboxFactory = Durables.createMailboxFactory("org.agilewiki.pactor.util.durable");
        registerFactories(mailboxFactory);
        return mailboxFactory;
    }

    public static void registerFactories(final MailboxFactory _mailboxFactory) throws Exception {
        FactoryLocator factoryLocator = Durables.getFactoryLocator(_mailboxFactory);
        registerFactories(factoryLocator);
    }

    public static void registerFactories(final FactoryLocator _factoryLocator) throws Exception {
        IncDesFactory.registerFactory(_factoryLocator);

        PABooleanImpl.registerFactory(_factoryLocator);
        PAIntegerImpl.registerFactory(_factoryLocator);
        PALongImpl.registerFactory(_factoryLocator);
        PAFloatImpl.registerFactory(_factoryLocator);
        PADoubleImpl.registerFactory(_factoryLocator);

        BoxImpl.registerFactory(_factoryLocator);
        RootImpl.registerFactory(_factoryLocator);
        PAStringImpl.registerFactory(_factoryLocator);
        BytesImpl.registerFactory(_factoryLocator);
    }

    public static void registerUnionFactory(final FactoryLocator _factoryLocator,
                                            final String _subActorType,
                                            final String... _actorTypes)
            throws Exception {
        UnionImpl.registerFactory(_factoryLocator, _subActorType, _actorTypes);
    }
}
