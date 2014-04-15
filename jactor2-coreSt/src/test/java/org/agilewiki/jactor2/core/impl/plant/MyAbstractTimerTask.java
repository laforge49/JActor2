/*
 * Copyright (C) 2014 Sebastien Diot.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.agilewiki.jactor2.core.impl.plant;

import java.util.concurrent.Delayed;
import java.util.concurrent.ScheduledFuture;

/**
 * @author monster
 */
public abstract class MyAbstractTimerTask<V> extends java.util.TimerTask
        implements ScheduledFuture<V> {

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public int compareTo(final Delayed o) {
        return Long.compare(scheduledExecutionTime(),
                ((MyAbstractTimerTask<V>) o).scheduledExecutionTime());
    }
}
