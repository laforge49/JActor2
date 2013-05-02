package org.agilewiki.jactor.util.durable;

import org.agilewiki.jactor.api.Mailbox;
import org.agilewiki.jactor.util.Ancestor;
import org.agilewiki.jactor.util.Named;

public interface Factory extends Named {
    PASerializable newSerializable(final Mailbox _mailbox) throws Exception;

    PASerializable newSerializable(final Mailbox _mailbox, final Ancestor _parent) throws Exception;

    String getFactoryKey();
}
