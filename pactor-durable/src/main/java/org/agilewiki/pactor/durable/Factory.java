package org.agilewiki.pactor.durable;

import org.agilewiki.pactor.api.Mailbox;
import org.agilewiki.pautil.Ancestor;
import org.agilewiki.pautil.Named;

public interface Factory extends Named {
    PASerializable newSerializable(final Mailbox _mailbox)
            throws Exception;

    PASerializable newSerializable(final Mailbox _mailbox, final Ancestor _parent);

    String getFactoryKey();
}
