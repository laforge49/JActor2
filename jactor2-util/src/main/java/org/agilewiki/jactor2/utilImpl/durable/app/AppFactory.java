package org.agilewiki.jactor2.utilImpl.durable.app;

import org.agilewiki.jactor2.core.processing.Mailbox;
import org.agilewiki.jactor2.util.Ancestor;
import org.agilewiki.jactor2.util.durable.Durables;
import org.agilewiki.jactor2.util.durable.Factory;
import org.agilewiki.jactor2.util.durable.FactoryLocator;
import org.agilewiki.jactor2.util.durable.app.App;
import org.agilewiki.jactor2.utilImpl.durable.FactoryImpl;

/**
 * Creates DurableImpl objects.
 */
public abstract class AppFactory extends FactoryImpl {
    private Factory[] tupleFactories;
    private String[] jidTypes;

    public AppFactory(String subActorType) {
        super(subActorType);
        this.jidTypes = new String[0];
    }

    /**
     * Create a JLPCActorFactory.
     *
     * @param subJidType The jid type.
     * @param jidTypes   The element types.
     */
    public AppFactory(String subJidType, String... jidTypes) {
        super(subJidType);
        this.jidTypes = jidTypes;
    }

    @Override
    abstract protected App instantiateActor() throws Exception;

    /**
     * Create and configure an actor.
     *
     * @param mailbox The processing of the new actor.
     * @param parent  The parent of the new actor.
     * @return The new actor.
     */
    @Override
    public App newSerializable(Mailbox mailbox, Ancestor parent) throws Exception {
        App a = instantiateActor();
        DurableImpl tj = new DurableImpl();
        a.setDurable(tj);
        tj.initialize(mailbox, parent, this);
        FactoryLocator fl = Durables.getFactoryLocator(mailbox);
        Factory[] afs = new FactoryImpl[jidTypes.length];
        int i = 0;
        while (i < jidTypes.length) {
            afs[i] = fl.getFactory(jidTypes[i]);
            i += 1;
        }
        tupleFactories = afs;
        tj.tupleFactories = tupleFactories;
        return a;
    }
}
