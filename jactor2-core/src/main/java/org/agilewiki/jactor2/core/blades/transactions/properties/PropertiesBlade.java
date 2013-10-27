package org.agilewiki.jactor2.core.blades.transactions.properties;

import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.blades.transactions.*;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

import java.util.SortedMap;

public class PropertiesBlade extends BladeBase {

    final private PropertiesProcessor propertiesProcessor;

    public PropertiesBlade(final NonBlockingReactor _reactor) throws Exception {
        initialize(_reactor);
        propertiesProcessor = new PropertiesProcessor(new IsolationReactor(_reactor.getFacility()), _reactor);
    }

    public Transaction<PropertiesWrapper> putTransaction(final String _key, final Object _newValue) {
        return new Transaction<PropertiesWrapper>() {
            @Override
            public AsyncRequest<Void> updateAReq(final PropertiesWrapper _stateWrapper) {
                return new AsyncBladeRequest<Void>() {
                    @Override
                    protected void processAsyncRequest() throws Exception {
                        _stateWrapper.put(_key, _newValue);
                        processAsyncResponse(null);
                    }
                };
            }
        };
    }

    public AsyncRequest<String> putAReq(final String _key, final Object _newValue) {
        return new AsyncBladeRequest<String>() {
            @Override
            protected void processAsyncRequest() throws Exception {
                Transaction<PropertiesWrapper> putTran = putTransaction(_key, _newValue);
                send(propertiesProcessor.processTransactionAReq(putTran), this);
            }
        };
    }

    public SortedMap<String, Object> getImmutableState() {
        return propertiesProcessor.getImmutableState();
    }

    public AsyncRequest<ValidationSubscription<PropertyChanges>> addValidatorAReq(
            final Validator<PropertyChanges> _validator) {
        return propertiesProcessor.addValidatorAReq(_validator);
    }

    public AsyncRequest<ChangeSubscription<PropertyChanges>> addChangeNotificationSubscriberAReq(
            final ChangeNotificationSubscriber<PropertyChanges> _changeNotificationSubscriber) {
        return propertiesProcessor.addChangeNotificationSubscriberAReq(_changeNotificationSubscriber);
    }
}
