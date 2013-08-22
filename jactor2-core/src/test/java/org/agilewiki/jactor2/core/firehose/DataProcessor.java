package org.agilewiki.jactor2.core.firehose;

import org.agilewiki.jactor2.core.Actor;
import org.agilewiki.jactor2.core.messaging.Request;

public interface DataProcessor extends Actor {

    Request<Void> processDataReq(final FirehoseData _firehoseData);
}
