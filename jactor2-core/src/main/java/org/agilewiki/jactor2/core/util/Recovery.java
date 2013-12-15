package org.agilewiki.jactor2.core.util;

import org.agilewiki.jactor2.core.messages.Message;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.reactors.ReactorBase;

public class Recovery {

    public long getReactorPollMillis() {
        return 1000;
    }

    public long messageTimeoutMillis() {
        return 3000;
    }

    public void messageTimeout(ReactorBase _reactor) throws Exception {
        _reactor.getLog().error("message timeout -> reactor close");
        _reactor.close();
    }

    public long getThreadInterruptMillis(final Reactor _reactor) {
        return 1000;
    }

    public void hungThread(ReactorBase _reactor) {
        _reactor.getFacility().getPlantImpl().forceExit();
    }

    public void hungResponse(final Message _message) throws Exception {
        ReactorBase reactor = (ReactorBase) _message.getTargetReactor();
        reactor.getLog().error("request hung -> reactor close");
        reactor.close();
    }
}
