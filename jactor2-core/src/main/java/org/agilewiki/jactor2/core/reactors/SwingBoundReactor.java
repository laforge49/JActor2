package org.agilewiki.jactor2.core.reactors;

import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.plant.BasicPlant;

import javax.swing.*;
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
public class SwingBoundReactor extends ReactorBase implements CommonReactor, WindowListener {

    public SwingBoundReactor(final BasicPlant _plant) throws Exception {
        this(_plant.asFacility());
    }

    /**
     * Create a targetReactor bound to the Swing event-dispatch thread.
     *
     * @param _facility The facility of the targetReactor.
     */
    public SwingBoundReactor(final Facility _facility) throws Exception {
        this(_facility, _facility.asFacilityImpl().getInitialBufferSize(), _facility.asFacilityImpl()
                .getInitialLocalMessageQueueSize());

    }

    public SwingBoundReactor(final BasicPlant _plant,
                             final int _initialOutboxSize, final int _initialLocalQueueSize)
            throws Exception {
        this(_plant.asFacility(), _initialOutboxSize, _initialLocalQueueSize);
    }

    /**
     * Create a targetReactor bound to the Swing event-dispatch thread.
     *
     * @param _facility              The facility of the targetReactor.
     * @param _initialOutboxSize     Initial size of the outbox for each unique message destination.
     * @param _initialLocalQueueSize The initial number of slots in the doLocal queue.
     */
    public SwingBoundReactor(final Facility _facility,
                             final int _initialOutboxSize, final int _initialLocalQueueSize)
            throws Exception {
        super(new SwingBoundReactorImpl(_facility, _initialOutboxSize, _initialLocalQueueSize));
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
            getFacility().close();
        } catch (final Exception ex) {
            getLog().warn("Exception when closing Facility", ex);
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
