package org.agilewiki.jactor2.core.blades.pubSub;

import org.agilewiki.jactor2.core.blades.IsolationBladeBase;
import org.agilewiki.jactor2.core.blades.filters.Filter;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.messages.*;
import org.agilewiki.jactor2.core.messages.impl.AsyncRequestImpl;
import org.agilewiki.jactor2.core.messages.impl.RequestImpl;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A blade that publishes content to interested subscribers, using either signals or sends.
 *
 * @param <CONTENT> The type of content.
 */
public class RequestBus<CONTENT> extends IsolationBladeBase {
    final Map<Subscription<CONTENT>, Boolean> subscriptions = new ConcurrentHashMap<Subscription<CONTENT>, Boolean>();

    public boolean noSubscriptions() {
        return subscriptions.isEmpty();
    }

    public RequestBus() throws Exception {
    }

    public RequestBus(final IsolationReactor _reactor) {
        super(_reactor);
    }

    private void signalContent(final CONTENT _content) {
        final Iterator<Subscription<CONTENT>> it = subscriptions.keySet()
                .iterator();
        while (it.hasNext()) {
            final Subscription<CONTENT> subscription = it.next();
            final Filter<CONTENT> filter = subscription.filter;
            if (filter.match(_content)) {
                subscription.publicationAOp(_content).signal();
            }
        }
    }

    /**
     * Sends some content to all the interested subscribers via a signal.
     *
     * @param _content
     * @param _sourceReactor
     */
    public void signalContent(final CONTENT _content,
            final IsolationReactor _sourceReactor) {
        directCheck(_sourceReactor);
        signalContent(_content);
    }

    /**
     * Returns a request to send some content to all the interested subscribers
     * without waiting for those subscribers to process the content.
     *
     * @param _content The content to be published.
     * @return The request.
     */
    public SOp<Void> signalsContentSOp(final CONTENT _content) {
        return new SOp<Void>("signalsContent", getReactor()) {
            @Override
            protected Void processSyncOperation(RequestImpl _requestImpl) throws Exception {
                signalContent(_content);
                return null;
            }
        };
    }

    /**
     * Returns a request to send some content to all the interested subscribers.
     *
     * @param _content The content to be published.
     * @return The request.
     */
    public AOp<Void> sendsContentAOp(final CONTENT _content) {
        return new SAOp<Void>("sendsContent", getReactor()) {
            final AsyncResponseProcessor<Void> dis = this;

            int count;
            int i;

            final AsyncResponseProcessor<Void> sendResponse = new AsyncResponseProcessor<Void>() {
                @Override
                public void processAsyncResponse(final Void _response)
                        throws Exception {
                    i++;
                    if (i == count) {
                        dis.processAsyncResponse(null);
                    }
                }
            };

            @Override
            protected void processAsyncOperation(AsyncRequestImpl _asyncRequestImpl) throws Exception {
                _asyncRequestImpl.setNoHungRequestCheck();
                count = subscriptions.size();
                final Iterator<Subscription<CONTENT>> it = subscriptions
                        .keySet().iterator();
                while (it.hasNext()) {
                    final Subscription<CONTENT> subscription = it.next();
                    final Filter<CONTENT> filter = subscription.filter;
                    if (filter.match(_content)) {
                        _asyncRequestImpl.send(subscription.publicationAOp(_content),
                                sendResponse);
                    } else {
                        count--;
                    }
                }
                if (count == 0) {
                    dis.processAsyncResponse(null);
                    return;
                }
            }
        };
    }

    public AIOp<Void> signalContentAOp(final CONTENT _content) {
        return new AIOp<Void>("signalContent", getReactor()) {
            @Override
            protected void processAsyncOperation(AsyncRequestImpl _asyncRequestImpl,
                                                 AsyncResponseProcessor<Void> _asyncResponseProcessor)
                    throws Exception {
                final Iterator<Subscription<CONTENT>> it = subscriptions
                        .keySet().iterator();
                while (it.hasNext()) {
                    final Subscription<CONTENT> subscription = it.next();
                    final Filter<CONTENT> filter = subscription.filter;
                    if (filter.match(_content)) {
                        subscription.publicationAOp(_content).signal();
                    }
                }
                _asyncResponseProcessor.processAsyncResponse(null);
            }
        };
    }
}
