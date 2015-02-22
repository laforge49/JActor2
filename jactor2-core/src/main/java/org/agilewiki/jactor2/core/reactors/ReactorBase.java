package org.agilewiki.jactor2.core.reactors;

import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.messages.SOp;
import org.agilewiki.jactor2.core.plant.impl.MetricsTimer;
import org.agilewiki.jactor2.core.plant.impl.PlantImpl;
import org.agilewiki.jactor2.core.reactors.closeable.Closeable;
import org.agilewiki.jactor2.core.reactors.closeable.impl.CloseableImpl;
import org.agilewiki.jactor2.core.reactors.impl.ReactorImpl;

/**
 * Base class for reactors.
 */
abstract public class ReactorBase extends BladeBase implements Reactor {
    /**
     * Returns the reactor of the current thread.
     *
     * @return The reactor of the current thread, or null.
     */
    public static Reactor getCurrentReactor() {
        return PlantImpl.getSingleton().getCurrentReactorImpl().asReactor();
    }

    private ReactorImpl reactorImpl;

    /**
     * Initialize the reactor.
     *
     * @param _reactorImpl The object used to implement the reactor.
     */
    public void initialize(final ReactorImpl _reactorImpl) throws Exception {
        if (_reactorImpl != null) {
            reactorImpl = _reactorImpl;
        }
        _reactorImpl.initialize(this);
        _initialize(this);
    }

    @Override
    public ReactorImpl asReactorImpl() {
        return reactorImpl;
    }

    @Override
    public CloseableImpl asCloseableImpl() {
        return reactorImpl.asCloseableImpl();
    }

    @Override
    public IsolationReactor getParentReactor() {
        return asReactorImpl().getParentReactor();
    }

    @Override
    public boolean addCloseable(final Closeable _closeable) {
        return reactorImpl.addCloseable(_closeable);
    }

    @Override
    public boolean removeCloseable(final Closeable _closeable) {
        return reactorImpl.removeCloseable(_closeable);
    }

    @Override
    public void close() throws Exception {
        reactorImpl.close();
    }

    @Override
    public void fail(final String _reason) throws Exception {
        reactorImpl.fail(_reason);
    }

    @Override
    public boolean isInboxEmpty() {
        return asReactorImpl().isInboxEmpty();
    }

    @Override
    public SOp<Void> nullSOp() {
        return asReactorImpl().nullSOp();
    }

    /**
     * Log a message at the WARN level.
     *
     * @param msg the message string to be logged
     */
    @Override
    public void warn(final String msg) {
        asReactorImpl().warn(msg);
    }

    /**
     * Log an exception (throwable) at the WARN level with an
     * accompanying message.
     *
     * @param msg the message accompanying the exception
     * @param t   the exception (throwable) to log
     */
    @Override
    public void warn(final String msg, final Throwable t) {
        asReactorImpl().warn(msg, t);
    }

    /**
     * Log a message at the ERROR level.
     *
     * @param msg the message string to be logged
     */
    @Override
    public void error(final String msg) {
        asReactorImpl().error(msg);
    }

    /**
     * Log an exception (throwable) at the ERROR level with an
     * accompanying message.
     *
     * @param msg the message accompanying the exception
     * @param t   the exception (throwable) to log
     */
    @Override
    public void error(final String msg, final Throwable t) {
        asReactorImpl().error(msg, t);
    }

    @Override
    public void addResource(Reactor _reactor) {
        asReactorImpl().addResource(_reactor.asReactorImpl());
    }

    @Override
    public boolean isResource(Reactor _reactor) {
        return asReactorImpl().isResource(_reactor.asReactorImpl());
    }

    public MetricsTimer getMetricsTimer(final String _name) {
        return reactorImpl.getMetricsTimer(_name);
    }
}
