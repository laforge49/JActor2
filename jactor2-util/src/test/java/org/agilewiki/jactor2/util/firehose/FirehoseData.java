package org.agilewiki.jactor2.util.firehose;

import org.agilewiki.jactor2.core.messaging.BoundResponseProcessor;

import java.util.List;

public class FirehoseData {
    private final BoundResponseProcessor<Void> ack;
    private final List<Long> content;

    public FirehoseData(final BoundResponseProcessor<Void> _ack, final List<Long> _content) {
        ack = _ack;
        content = _content;
    }

    public BoundResponseProcessor<Void> getAck() {
        return ack;
    }

    public List<Long> getContent() {
        return content;
    }
}
