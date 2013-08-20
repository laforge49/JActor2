package org.agilewiki.jactor2.core.processing;

import org.agilewiki.jactor2.core.ActorBase;
import org.agilewiki.jactor2.core.context.JAContext;
import org.agilewiki.jactor2.core.messaging.Request;
import org.agilewiki.jactor2.core.messaging.Transport;

import javax.swing.*;

public class SwingBoundMessageProcessorSample {
    public static void main(final String[] _args) throws Exception {
        HelloWorld helloWorld = new HelloWorld(new SwingBoundMessageProcessor(new JAContext(0)));
        helloWorld.createAndShowReq().signal();
    }
}

class HelloWorld extends ActorBase {
    HelloWorld(final SwingBoundMessageProcessor _messageProcessor) throws Exception{
        initialize(_messageProcessor);
    }

    Request<Void> createAndShowReq() {
        return new Request<Void>(getMessageProcessor()) {
            @Override
            public void processRequest(Transport<Void> _transport) throws Exception {
                //Create and set up the window.
                JFrame frame = new JFrame("HelloWorldSwing");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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