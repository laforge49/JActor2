package org.agilewiki.jactor2.core.reactors;

import org.agilewiki.jactor2.core.GwtIncompatible;
import org.agilewiki.jactor2.core.blades.SwingBoundBlade;
import org.agilewiki.jactor2.core.plant.impl.PlantBase;
import org.agilewiki.jactor2.core.plant.impl.PlantImpl;
import org.agilewiki.jactor2.core.reactors.impl.ReactorImpl;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * A reactor which processes requests/responses on the Swing UI thread.
 * <p>
 * Requests/responses are processed one at a time in the order received, except that
 * requests/responses from the same reactor are given preference.
 * </p>
 * <p>
 * Requests/responses destined to a different reactor are held until all
 * incoming messages have been processed.
 * </p>
 */
@GwtIncompatible
public class SwingBoundReactor extends ThreadBoundReactor implements
        WindowListener, SwingBoundBlade {

    /**
     * Create a swing-bound reactor with the Plant internal reactor as the parent.
     */
    public SwingBoundReactor() throws Exception {
        super(PlantBase.getInternalFacility());
    }

    /**
     * Create a Swing-bound reactor.
     *
     * @param _parentReactor            The parent reactor.
     */
    public SwingBoundReactor(final IsolationReactor _parentReactor)
            throws Exception {
        this(_parentReactor, _parentReactor.asReactorImpl()
                .getInitialBufferSize(), _parentReactor.asReactorImpl()
                .getInitialLocalQueueSize());
    }

    /**
     * Create a Swing-bound reactor with the Plant internal reactor as the parent.
     *
     * @param _initialOutboxSize        Initial size of the list of requests/responses for each destination.
     * @param _initialLocalQueueSize    Initial size of the local input queue.
     */
    public SwingBoundReactor(final int _initialOutboxSize,
            final int _initialLocalQueueSize) throws Exception {
        this(PlantBase.getInternalFacility(), _initialOutboxSize,
                _initialLocalQueueSize);
    }

    /**
     * Create a Swing-bound reactor.
     *
     * @param _parentReactor            The parent reactor.
     * @param _initialOutboxSize        Initial size of the list of requests/responses for each destination.
     * @param _initialLocalQueueSize    Initial size of the local input queue.
     */
    public SwingBoundReactor(final IsolationReactor _parentReactor,
            final int _initialOutboxSize, final int _initialLocalQueueSize)
            throws Exception {
        super(_parentReactor, _initialOutboxSize, _initialLocalQueueSize, null);
    }

    @Override
    protected ReactorImpl createReactorImpl(
            final IsolationReactor _parentReactor,
            final int _initialOutboxSize, final int _initialLocalQueueSize,
            final Runnable _boundProcessor) {
        return PlantImpl.getSingleton().createSwingBoundReactorImpl(
                _parentReactor, _initialOutboxSize, _initialLocalQueueSize);
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void windowOpened(final WindowEvent e) {
    }

    @Override
    public void windowClosing(final WindowEvent e) {
    }

    /**
     * Closes the plant when the window is closed.
     * @param e    A window event.
     */
    @Override
    public void windowClosed(final WindowEvent e) {
        try {
            PlantBase.close();
        } catch (final Exception ex) {
            asReactorImpl().warn("Exception when closing Facility", ex);
        }
    }

    @Override
    public void windowIconified(final WindowEvent e) {
    }

    @Override
    public void windowDeiconified(final WindowEvent e) {
    }

    @Override
    public void windowActivated(final WindowEvent e) {
    }

    @Override
    public void windowDeactivated(final WindowEvent e) {
    }

    @Override
    public SwingBoundReactor getReactor() {
        return this;
    }
}
