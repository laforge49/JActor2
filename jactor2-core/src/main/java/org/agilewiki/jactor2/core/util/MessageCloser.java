package org.agilewiki.jactor2.core.util;

import org.agilewiki.jactor2.core.messages.Message;
import org.agilewiki.jactor2.core.plant.ServiceClosedException;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

abstract public class MessageCloser extends CloserBase {
    private Set<Message> messages = new HashSet<Message>();

    protected boolean addMessage(final Message _message) throws ServiceClosedException {
        return messages.add(_message);
    }

    protected boolean removeMessage(final Message _message) {
        return messages.remove(_message);
    }

    protected void closeAll() throws Exception {
        Iterator<Message> it = messages.iterator();
        while (it.hasNext()) {
            Message message = it.next();
            message.close();
        }
        super.closeAll();
    }
}
