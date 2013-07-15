package org.agilewiki.jactor2.util.firehose;

import org.agilewiki.jactor2.api.Actor;
import org.agilewiki.jactor2.api.BoundRequest;

public interface DataProcessor extends Actor {

    BoundRequest<Void> processDataReq(final FirehoseData _firehoseData);
}
