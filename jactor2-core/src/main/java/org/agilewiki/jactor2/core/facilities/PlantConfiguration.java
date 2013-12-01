package org.agilewiki.jactor2.core.facilities;

import org.agilewiki.jactor2.core.reactors.Inbox;
import org.agilewiki.jactor2.core.reactors.Outbox;
import org.agilewiki.jactor2.core.util.DefaultRecovery;
import org.agilewiki.jactor2.core.util.Recovery;

import java.util.concurrent.ThreadFactory;

public class PlantConfiguration {
    public final static int DEFAULT_THREAD_COUNT = 20;
    public final int threadPoolSize;

    public PlantConfiguration() {
        threadPoolSize = DEFAULT_THREAD_COUNT;
    }

    public PlantConfiguration(final int _threadPoolSize) {
        threadPoolSize = _threadPoolSize;
    }

    public ThreadFactory getThreadFactory() {
        return new DefaultThreadFactory();
    }

    public ThreadManager getThreadManager() {
        return new ThreadManager(threadPoolSize, getThreadFactory());
    }

    public Recovery getRecovery() {
        return new DefaultRecovery();
    }

    public int getInitialLocalMessageQueueSize() {
        return Inbox.DEFAULT_INITIAL_LOCAL_QUEUE_SIZE;
    }

    public int getInitialBufferSize() {
        return Outbox.DEFAULT_INITIAL_BUFFER_SIZE;
    }
}
