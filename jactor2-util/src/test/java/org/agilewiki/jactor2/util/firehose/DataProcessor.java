package org.agilewiki.jactor2.util.firehose;

import org.agilewiki.jactor2.core.Actor;
import org.agilewiki.jactor2.core.Request;

public interface DataProcessor extends Actor {

    Request<Void> processDataReq(final FirehoseData _firehoseData);
}
