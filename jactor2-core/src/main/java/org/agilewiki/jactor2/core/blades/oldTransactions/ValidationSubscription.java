package org.agilewiki.jactor2.core.blades.oldTransactions;

import org.agilewiki.jactor2.core.blades.oldRequestBus.Subscription;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

public class ValidationSubscription<STATE, STATE_WRAPPER extends AutoCloseable, IMMUTABLE_CHANGES extends ImmutableChanges, IMMUTABLE_STATE>
        extends Subscription<IMMUTABLE_CHANGES, Void> {
    public final Validator<IMMUTABLE_CHANGES> validator;

    public ValidationSubscription(
            final Validator<IMMUTABLE_CHANGES> _validator,
            final ValidationBus<STATE, STATE_WRAPPER, IMMUTABLE_CHANGES, IMMUTABLE_STATE> _validationBus)
            throws Exception {
        super((NonBlockingReactor) _validator.getReactor(), _validationBus);
        validator = _validator;
    }

    @Override
    public AsyncRequest<Void> notificationAReq(final IMMUTABLE_CHANGES _content) {
        return validator.validateAReq(_content);
    }
}
