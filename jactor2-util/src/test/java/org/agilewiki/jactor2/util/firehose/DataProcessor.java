package org.agilewiki.jactor2.util.firehose;

import org.agilewiki.jactor2.api.Actor;
import org.agilewiki.jactor2.api.Request;

public interface DataProcessor extends Actor {

    Request<Void> processDataReq(final FirehoseData _firehoseData);
}
