package org.agilewiki.jactor.util.firehose;

public class Passer extends StageBase {

    public Passer(FirehoseMailbox _mailbox) {
        super(_mailbox);
    }

    @Override
    public Object process(Engine _engine, Object data) {
        return data;
    }
}
