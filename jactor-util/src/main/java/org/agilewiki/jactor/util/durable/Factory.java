package org.agilewiki.jactor.util.durable;

import org.agilewiki.jactor.api.Mailbox;
import org.agilewiki.jactor.util.Ancestor;
import org.agilewiki.jactor.util.Named;

public interface Factory extends Named {
    JASerializable newSerializable(final Mailbox _mailbox) throws Exception;

    JASerializable newSerializable(final Mailbox _mailbox, final Ancestor _parent) throws Exception;

    String getFactoryKey();
}
