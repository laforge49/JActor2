package org.agilewiki.jactor2.core.util;

import org.agilewiki.jactor2.core.impl.ReactorImpl;
import org.agilewiki.jactor2.core.impl.RequestImpl;
import org.agilewiki.jactor2.core.plant.BasicPlant;

public class Recovery {

    public long getReactorPollMillis() {
        return 1000;
    }

    public long messageTimeoutMillis() {
        return 60000;
    }

    public void messageTimeout(ReactorImpl _reactor) throws Exception {
        _reactor.getLogger().error("message timeout -> reactor close");
        _reactor.close();
    }

    public long getThreadInterruptMillis(final ReactorImpl _reactor) {
        return 1000;
    }

    public void hungThread(ReactorImpl _reactor) {
        BasicPlant.getSingleton().exit();
    }

    public void hungResponse(final RequestImpl _message) throws Exception {
        ReactorImpl reactor = _message.getTargetReactorImpl().asReactorImpl();
        reactor.getLogger().error("request hung -> reactor close");
        reactor.close();
    }
}
