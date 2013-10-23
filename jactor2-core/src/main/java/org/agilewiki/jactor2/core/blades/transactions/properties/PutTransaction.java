package org.agilewiki.jactor2.core.blades.transactions.properties;

import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.blades.transactions.Transaction;
import org.agilewiki.jactor2.core.messages.AsyncRequest;

public class PutTransaction extends BladeBase implements Transaction<PropertiesWrapper> {
    final public String key;
    final public Object newValue;

    public PutTransaction(final String _key, final Object _newValue) {
        key = _key;
        newValue = _newValue;
    }

    @Override
    public AsyncRequest<Void> updateAReq(final PropertiesWrapper _stateWrapper) {
        return new AsyncBladeRequest<Void>() {
            @Override
            protected void processAsyncRequest() throws Exception {
                _stateWrapper.put(key, newValue);
                processAsyncResponse(null);
            }
        };
    }
}
