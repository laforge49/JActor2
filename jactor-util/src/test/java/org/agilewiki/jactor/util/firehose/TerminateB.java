package org.agilewiki.jactor.util.firehose;

import java.util.List;

public class TerminateB extends StageBase {

    private long count;

    private Thread thread;

    public TerminateB(FirehoseMailbox _mailbox, final long _count, final Thread _thread) {
        super(_mailbox);
        count = _count;
        thread = _thread;
    }

    @Override
    public Object process(Engine _engine, Object data) {
        List<Long> lst = (List<Long>) data;
        if (lst.size() == 0)
            return null;
        long i = lst.get(lst.size() - 1);
        if (i == count) {
            thread.interrupt();
        }
        return null;
    }
}
