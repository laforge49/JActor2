package org.agilewiki.pactor.util.durable;

import org.agilewiki.pactor.api.Mailbox;
import org.agilewiki.pactor.util.Ancestor;
import org.agilewiki.pactor.util.Named;

public interface Factory extends Named {
    PASerializable newSerializable(final Mailbox _mailbox)
            throws Exception;

    PASerializable newSerializable(final Mailbox _mailbox, final Ancestor _parent);

    String getFactoryKey();
}
