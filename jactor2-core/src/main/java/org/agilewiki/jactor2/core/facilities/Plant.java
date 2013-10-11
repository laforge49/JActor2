package org.agilewiki.jactor2.core.facilities;

import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.EventBus;
import org.agilewiki.jactor2.core.reactors.Inbox;
import org.agilewiki.jactor2.core.reactors.Outbox;

import java.util.concurrent.ThreadFactory;

public class Plant extends Facility {

    /**
     * Create a Plant.
     */
    public Plant() throws Exception {
        this(
                Inbox.DEFAULT_INITIAL_LOCAL_QUEUE_SIZE,
                Outbox.DEFAULT_INITIAL_BUFFER_SIZE,
                20,
                new DefaultThreadFactory());
    }

    /**
     * Create a Plant.
     *
     * @param _threadCount The thread pool size.
     */
    public Plant(final int _threadCount) throws Exception {
        this(
                Inbox.DEFAULT_INITIAL_LOCAL_QUEUE_SIZE,
                Outbox.DEFAULT_INITIAL_BUFFER_SIZE,
                _threadCount,
                new DefaultThreadFactory());
    }

    /**
     * Create a Plant.
     *
     * @param _initialLocalMessageQueueSize How big should the initial inbox doLocal queue size be?
     * @param _initialBufferSize            How big should the initial outbox (per target Reactor) buffer size be?
     * @param _threadCount                  The thread pool size.
     * @param _threadFactory                The factory used to create threads for the threadpool.
     */
    public Plant(final int _initialLocalMessageQueueSize,
                    final int _initialBufferSize,
                    final int _threadCount,
                    final ThreadFactory _threadFactory) throws Exception {
        super(_initialLocalMessageQueueSize, _initialBufferSize, _threadCount, _threadFactory);
        firstSet(NAME_PROPERTY, PLANT_NAME);
    }

    public AsyncRequest<Void> dependencyAReq(final Facility _dependency) {
        return new AsyncBladeRequest<Void>() {
            @Override
            protected void processAsyncRequest() throws Exception {
                throw new UnsupportedOperationException("Plant can have no dependencies");
            }
        };
    }
}
