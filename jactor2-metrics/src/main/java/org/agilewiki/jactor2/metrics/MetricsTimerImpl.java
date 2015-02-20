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
package org.agilewiki.jactor2.metrics;

import com.codahale.metrics.Clock;
import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import org.agilewiki.jactor2.core.plant.impl.MetricsTimer;

import java.util.SortedMap;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * A metrics Timer, that also tracks failures.
 *
 * @author monster
 */
public class MetricsTimerImpl extends com.codahale.metrics.Timer implements MetricsTimer {
    /**
     * We always use the default Clock
     */
    private static final Clock CLOCK = Clock.defaultClock();

    /**
     * The metric registry used.
     */
    public static final MetricRegistry REGISTRY = new MetricRegistry();

    /**
     * The ConsoleReporter.
     */
    private static volatile ConsoleReporter reporter;

    private static volatile int nextHash;

    /**
     * Sets up a ConsoleReporter, if not yet setup.
     */
    public static void setupConsoleReporter(final long reportEveryMillis) {
        if (reporter == null) {
            reporter = ConsoleReporter.forRegistry(REGISTRY)
                    .convertDurationsTo(TimeUnit.MILLISECONDS)
                    .convertRatesTo(TimeUnit.SECONDS).build();
            reporter.start(reportEveryMillis, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * Meter, for failed requests/calls/executions/events...
     */
    public final Meter failed;

    /**
     * The name.
     */
    public final String name;

    /**
     * Our hashcode.
     */
    private final int hashCode = nextHash++;

    /**
     * Returns a Timer for the given class.
     * <p/>
     * It is recommended, for performance reasons, to only call this method
     * once per each type/names pair.
     *
     * @param name the name
     * @return A Timer using {@code type} and {@code names} concatenated by periods as "name".
     */
    public static synchronized MetricsTimerImpl getMetricsTimer(String name) {
        final SortedMap<String, com.codahale.metrics.Timer> timers = REGISTRY
                .getTimers();
        final com.codahale.metrics.Timer prev = timers.get(name);
        final MetricsTimerImpl result;
        if (prev != null) {
            if (!(prev instanceof MetricsTimerImpl)) {
                throw new IllegalStateException("An instance of type "
                        + prev.getClass()
                        + " is already registered under the name " + name);
            }
            result = (MetricsTimerImpl) prev;
        } else {
            result = new MetricsTimerImpl(name);
            REGISTRY.register(name, result);
            REGISTRY.register(name + ".failed", result.failed);
        }
        return result;
    }

    public final long nanos() {
        return CLOCK.getTick();
    }

    /**
     * Creates a Timer.
     */
    private MetricsTimerImpl(final String name) {
        this.name = name;
        failed = new Meter(CLOCK);
    }

    /**
     * Redefines the hashcode for a faster hashing.
     */
    @Override
    public int hashCode() {
        return hashCode;
    }

    /**
     * Returns the name
     */
    @Override
    public String toString() {
        return name;
    }

    public final void updateNanos(final long nanos, final boolean success) {
        update(nanos, TimeUnit.NANOSECONDS);
        if (!success) {
            failed.mark();
        }
    }

    /**
     * Adds a recorded duration in nanoseconds.
     *
     * @param nanos   the length of the duration in nanoseconds
     * @param success True, if the execution succeeded.
     */
    public final void updateNanos(final double nanos, final boolean success) {
        updateNanos((long) nanos, success);
    }

    /**
     * Adds a recorded duration in milliseconds.
     *
     * @param millis  the length of the duration in milliseconds
     * @param success True, if the execution succeeded.
     */
    public final void updateMillis(final long millis, final boolean success) {
        updateNanos(millis * 1000000L, success);
    }

    /**
     * Adds a recorded duration in milliseconds.
     *
     * @param millis  the length of the duration in milliseconds
     * @param success True, if the execution succeeded.
     */
    public final void updateMillis(final double millis, final boolean success) {
        updateNanos((long) (millis * 1000000.0), success);
    }

    /**
     * Times and records the duration of event.
     *
     * @param event a {@link Callable} whose {@link Callable#call()} method implements a process
     *              whose duration should be timed
     * @param <T>   the type of the value returned by {@code event}
     * @return the value returned by {@code event}
     * @throws Exception if {@code event} throws an {@link Exception}
     */
    @Override
    public final <T> T time(final Callable<T> event) throws Exception {
        final long startTime = nanos();
        boolean success = false;
        try {
            final T result = event.call();
            success = true;
            return result;
        } finally {
            updateNanos(nanos() - startTime, success);
        }
    }

    /**
     * Times and records the duration of event.
     *
     * @param event a {@link Runnable} whose {@link Runnable#run()} method implements a process
     *              whose duration should be timed
     * @throws Exception if {@code event} throws an {@link Exception}
     */
    public final void time(final Runnable event) throws Exception {
        final long startTime = nanos();
        boolean success = false;
        try {
            event.run();
            success = true;
        } finally {
            updateNanos(nanos() - startTime, success);
        }
    }
}
