package org.agilewiki.jactor2.core.util;

import org.agilewiki.jactor2.core.messages.Message;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.reactors.ReactorImpl;

public class Recovery {

    public long getReactorPollMillis() {
        return 1000;
    }

    public long messageTimeoutMillis() {
        return 3000;
    }

    public void messageTimeout(ReactorImpl _reactor) throws Exception {
        _reactor.getLog().error("message timeout -> reactor close");
        _reactor.close();
    }

    public long getThreadInterruptMillis(final ReactorImpl _reactor) {
        return 1000;
    }

    public void hungThread(ReactorImpl _reactor) {
        _reactor.getFacility().getPlant().forceExit();
    }

    public void hungResponse(final Message _message) throws Exception {
        ReactorImpl reactor = _message.getTargetReactor().asReactorImpl();
        reactor.getLog().error("request hung -> reactor close");
        reactor.close();
    }
}
