package org.agilewiki.jactor2.core.impl.mtPlant;

import java.util.concurrent.ThreadFactory;

/**
 * Base class used to create the pool threads used by reactors.
 * Created by PlantConfiguration.
 */
public final class DefaultReactorPoolThreadFactory implements ThreadFactory {
    /**
     * The newThread method returns a newly created ReactorPoolThread.
     *
     * @param _runnable The run method is called when the thread is started.
     */
    @Override
    public ReactorPoolThread newThread(final Runnable _runnable) {
        return new ReactorPoolThread(_runnable);
    }
}
