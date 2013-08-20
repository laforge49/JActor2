package org.agilewiki.jactor2.core.processing;

import org.agilewiki.jactor2.core.ActorBase;
import org.agilewiki.jactor2.core.context.JAContext;
import org.agilewiki.jactor2.core.messaging.Request;
import org.agilewiki.jactor2.core.messaging.Transport;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SwingBoundMessageProcessorSample {
    public static void main(final String[] _args) throws Exception {

        //Create a context with 5 threads.
        JAContext context = new JAContext(5);

        HelloWorld helloWorld = new HelloWorld(new SwingBoundMessageProcessor(context));
        helloWorld.createAndShowReq().signal();
    }
}

class HelloWorld extends ActorBase {
    HelloWorld(final SwingBoundMessageProcessor _messageProcessor) throws Exception {
        initialize(_messageProcessor);
    }

    Request<Void> createAndShowReq() {
        return new Request<Void>(getMessageProcessor()) {
            @Override
            public void processRequest(Transport<Void> _transport) throws Exception {
                //Create and set up the window.
                JFrame frame = new JFrame("HelloWorldSwing");
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

                //Close context when window is closed.
                frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        try {
                            getMessageProcessor().getJAContext().close();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });

                //Add the ubiquitous "Hello World" label.
                JLabel label = new JLabel("Hello World");
                frame.getContentPane().add(label);

                //Display the window.
                frame.pack();
                frame.setVisible(true);
                _transport.processResponse(null);
            }
        };
    }

}