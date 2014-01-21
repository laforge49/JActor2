package org.agilewiki.jactor2.core.plant;

import org.agilewiki.jactor2.core.impl.BlockingReactorImpl;
import org.agilewiki.jactor2.core.impl.NonBlockingReactorImpl;
import org.agilewiki.jactor2.core.impl.ReactorImpl;
import org.agilewiki.jactor2.core.impl.RequestImpl;

public class Recovery {

    public long getReactorPollMillis() {
        return 500;
    }

    public long getMessageTimeoutMillis(final ReactorImpl _reactorImpl) {
        if (_reactorImpl instanceof BlockingReactorImpl)
            return 300000;
        return 1000;
    }

    public void onMessageTimeout(final ReactorImpl _reactorImpl) throws Exception {
        _reactorImpl.getLogger().error("message timeout -> reactor close");
        _reactorImpl.close();
    }

    public long getThreadInterruptMillis(final ReactorImpl _reactorImpl) {
        return 1000;
    }

    public void onHungThread(final ReactorImpl _reactorImpl) {
        Plant.exit();
    }

    public void onHungRequest(final RequestImpl _requestImpl) throws Exception {
        ReactorImpl reactor = _requestImpl.getTargetReactorImpl();
        reactor.getLogger().error("request hung -> reactor close");
        reactor.close();
    }
}
