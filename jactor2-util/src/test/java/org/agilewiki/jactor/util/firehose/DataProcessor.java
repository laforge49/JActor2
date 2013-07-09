package org.agilewiki.jactor.util.firehose;

import org.agilewiki.jactor.api.Actor;
import org.agilewiki.jactor.api.Request;

public interface DataProcessor extends Actor {

    Request<Void> processDataReq(final FirehoseData _firehoseData);
}
