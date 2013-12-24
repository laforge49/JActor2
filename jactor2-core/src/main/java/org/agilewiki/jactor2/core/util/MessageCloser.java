package org.agilewiki.jactor2.core.util;

import org.agilewiki.jactor2.core.impl.RequestImpl;
import org.agilewiki.jactor2.core.plant.ServiceClosedException;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

abstract public class MessageCloser extends CloserBase {
    private Set<RequestImpl> messages = new HashSet<RequestImpl>();

    protected boolean addMessage(final RequestImpl _message) throws ServiceClosedException {
        return messages.add(_message);
    }

    protected boolean removeMessage(final RequestImpl _message) {
        return messages.remove(_message);
    }

    protected void closeAll() throws Exception {
        Iterator<RequestImpl> it = messages.iterator();
        while (it.hasNext()) {
            RequestImpl message = it.next();
            message.close();
        }
        super.closeAll();
    }
}
