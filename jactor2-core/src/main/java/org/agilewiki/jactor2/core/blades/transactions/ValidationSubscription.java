package org.agilewiki.jactor2.core.blades.transactions;

import org.agilewiki.jactor2.core.blades.requestBus.Subscription;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class ValidationSubscription<IMMUTABLE_CHANGES> extends Subscription<IMMUTABLE_CHANGES, Void> {
    final private Validator<IMMUTABLE_CHANGES> validator;

    public ValidationSubscription(final Validator<IMMUTABLE_CHANGES> _validator,
                                  final ValidationBus<IMMUTABLE_CHANGES> _validationBus) throws Exception {
        super((NonBlockingReactor) _validator.getReactor(), _validationBus);
        validator = _validator;
    }

    @Override
    public AsyncRequest<Void> notificationAReq(IMMUTABLE_CHANGES _content) {
        return validator.validateAReq(_content);
    }
}
