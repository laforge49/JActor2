package org.agilewiki.jactor2.core;

import org.agilewiki.jactor2.core.processing.IsolationMessageProcessor;
import org.agilewiki.jactor2.core.threading.ModuleContext;

public class IsolationActor extends ActorBase {
    public IsolationActor(final ModuleContext _moduleContext) throws Exception {
        initialize(new IsolationMessageProcessor(_moduleContext));
    }
}
