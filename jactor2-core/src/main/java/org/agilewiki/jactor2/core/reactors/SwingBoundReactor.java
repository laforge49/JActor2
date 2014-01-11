package org.agilewiki.jactor2.core.reactors;

import org.agilewiki.jactor2.core.impl.NonBlockingReactorImpl;
import org.agilewiki.jactor2.core.impl.ReactorImpl;
import org.agilewiki.jactor2.core.impl.SwingBoundReactorImpl;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.plant.Scheduler;
import org.agilewiki.jactor2.core.util.Recovery;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * Messages are processed on Swing's event-dispatch thread when an blades uses
 * a SwingBoundReactor. This is critical, as so many Swing methods are
 * not thread-safe. Also, if each window has its own facility, then closing a
 * window and its facility will also terminate all activity related to that window.
 * <h3>Sample Usage:</h3>
 * <pre>
 * import org.agilewiki.jactor2.core.blades.BladeBase;
 * import org.agilewiki.jactor2.core.threading.Plant;
 * import org.agilewiki.jactor2.core.messaging.AsyncRequest;
 *
 * import javax.swing.*;
 *
 * public class SwingBoundMessageProcessorSample {
 *     public static void main(final String[] _args) throws Exception {
 *         new HelloWorld().createAndShowAReq().signal();
 *     }
 * }
 *
 * class HelloWorld extends BladeBase {
 *
 *     HelloWorld() throws Exception {
 *
 *         //Create a facility with 5 threads.
 *         Plant plant = new Plant(5);
 *
 *         initialize(new SwingBoundReactor(facility));
 *     }
 *
 *     AsyncRequest&lt;Void&gt; createAndShowAReq() {
 *         return new AsyncBladeRequest&lt;Void&gt;() {
 *             {@literal @}Override
 *             protected void processAsyncRequest() throws Exception {
 *                 //Create and set up the window.
 *                 JFrame frame = new JFrame("HelloWorld");
 *                 frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); //no exit until all threads are closed.
 *
 *                 //Close plant when window is closed.
 *                 frame.addWindowListener((SwingBoundReactor) targetReactor);
 *
 *                 //Add the "Hello World!" label.
 *                 JLabel label = new JLabel("Hello World!");
 *                 frame.getContentPane().add(label);
 *
 *                 //Display the window.
 *                 frame.pack();
 *                 frame.setVisible(true);
 *
 *                 //return the result.
 *                 processAsyncResponse(null);
 *             }
 *         };
 *     }
 *
 * }
 * </pre>
 */
public class SwingBoundReactor extends ThreadBoundReactor implements WindowListener {

    public SwingBoundReactor() throws Exception {
        super(Plant.getReactor());
    }

    public SwingBoundReactor(final NonBlockingReactor _parentReactor)
            throws Exception {
        this(_parentReactor, _parentReactor.asReactorImpl().getInitialBufferSize(),
                _parentReactor.asReactorImpl().getInitialLocalQueueSize());
    }

    public SwingBoundReactor(final int _initialOutboxSize, final int _initialLocalQueueSize)
            throws Exception {
        this(Plant.getReactor(), _initialOutboxSize, _initialLocalQueueSize);
    }

    public SwingBoundReactor(final NonBlockingReactor _parentReactor,
                              final int _initialOutboxSize, final int _initialLocalQueueSize)
            throws Exception {
        super(_parentReactor, _initialOutboxSize, _initialLocalQueueSize, null);
    }

    @Override
    protected ReactorImpl createReactorImpl(final NonBlockingReactorImpl _parentReactorImpl,
                                            final int _initialOutboxSize, final int _initialLocalQueueSize,
                                           final Runnable _boundProcessor) throws Exception {
        return new SwingBoundReactorImpl(_parentReactorImpl, _initialOutboxSize, _initialLocalQueueSize);
    }

    @Override
    public SwingBoundReactorImpl asReactorImpl() {
        return (SwingBoundReactorImpl) asCloserImpl();
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

    @Override
    public void windowClosed(final WindowEvent e) {
        try {
            Plant.close();
        } catch (final Exception ex) {
            asReactorImpl().getLogger().warn("Exception when closing Facility", ex);
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
}
