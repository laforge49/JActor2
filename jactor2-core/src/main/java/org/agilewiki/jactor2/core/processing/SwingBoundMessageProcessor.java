package org.agilewiki.jactor2.core.processing;

import org.agilewiki.jactor2.core.threading.Facility;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * Messages are processed on Swing's event-dispatch thread when an actor uses
 * a SwingBoundMessageProcessor. This is critical, as so many Swing methods are
 * not thread-safe. Also, if each window has its own facility, then closing a
 * window and its facility will also terminate all activity related to that window.
 * <h3>Sample Usage:</h3>
 * <pre>
 * import org.agilewiki.jactor2.core.ActorBase;
 * import org.agilewiki.jactor2.core.threading.Facility;
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
 * class HelloWorld extends ActorBase {
 *
 *     HelloWorld() throws Exception {
 *
 *         //Create a facility with 5 threads.
 *         Facility facility = new Facility(5);
 *
 *         initialize(new SwingBoundMessageProcessor(facility));
 *     }
 *
 *     AsyncRequest&lt;Void&gt; createAndShowAReq() {
 *         return new AsyncRequest&lt;Void&gt;(getMessageProcessor()) {
 *             {@literal @}Override
 *             public void processAsyncRequest() throws Exception {
 *                 //Create and set up the window.
 *                 JFrame frame = new JFrame("HelloWorld");
 *                 frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); //no exit until all threads are closed.
 *
 *                 //Close facility when window is closed.
 *                 frame.addWindowListener((SwingBoundMessageProcessor) getMessageProcessor());
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
public class SwingBoundMessageProcessor extends ThreadBoundMessageProcessor implements WindowListener {

    /**
     * Create a message processor processor bound to the Swing event-dispatch thread.
     *
     * @param _facility The facility of the message processor.
     */
    public SwingBoundMessageProcessor(Facility _facility) {
        super(_facility, null);

    }

    /**
     * Create a message processor bound to the Swing event-dispatch thread.
     *
     * @param _facility              The facility of the message processor.
     * @param _initialOutboxSize     Initial size of the outbox for each unique message destination.
     * @param _initialLocalQueueSize The initial number of slots in the local queue.
     */
    public SwingBoundMessageProcessor(Facility _facility, int _initialOutboxSize, int _initialLocalQueueSize) {
        super(_facility, _initialOutboxSize, _initialLocalQueueSize, null);
    }

    @Override
    protected void afterAdd() {
        SwingUtilities.invokeLater(this);
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
    }

    @Override
    public void windowClosed(WindowEvent e) {
        try {
            getFacility().close();
        } catch (Exception ex) {
            getLogger().warn("Exception when closing Facility", ex);
        }
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }
}
