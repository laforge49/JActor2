package org.agilewiki.jactor2.core.firehose;

import org.agilewiki.jactor2.core.Actor;
import org.agilewiki.jactor2.core.messaging.AsyncRequest;

public interface DataProcessor extends Actor {

    AsyncRequest<Void> processDataAReq(final FirehoseData _firehoseData);
}
