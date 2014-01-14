package org.agilewiki.jactor2.core.reactors;

import org.agilewiki.jactor2.core.impl.CloserImpl;

public class CloserBase implements Closer {
    private CloserImpl closerImpl;

    @Override
    public CloserImpl asCloserImpl() {
        return closerImpl;
    }

    protected void initialize(final CloserImpl _closerImpl) throws Exception {
        if (_closerImpl != null)
            closerImpl = _closerImpl;
    }

    @Override
    public boolean addCloseable(CloseableBase _closeable) throws Exception {
        return closerImpl.addCloseable(_closeable);
    }

    @Override
    public boolean removeCloseable(CloseableBase _closeable) {
        return closerImpl.removeCloseable(_closeable);
    }

    @Override
    public void close() throws Exception {
        closerImpl.close();
    }

    @Override
    public Reactor getReactor() {
        return closerImpl.getReactor();
    }
}
