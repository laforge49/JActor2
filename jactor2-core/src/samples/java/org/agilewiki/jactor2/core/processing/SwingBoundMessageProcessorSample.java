package org.agilewiki.jactor2.core.processing;

import org.agilewiki.jactor2.core.ActorBase;
import org.agilewiki.jactor2.core.messaging.AsyncRequest;
import org.agilewiki.jactor2.core.threading.ModuleContext;

import javax.swing.*;

public class SwingBoundMessageProcessorSample {
    public static void main(final String[] _args) throws Exception {

        new HelloWorld().createAndShowReq().signal();
    }
}

class HelloWorld extends ActorBase {
    HelloWorld() throws Exception {

        //Create a context with 5 threads.
        ModuleContext context = new ModuleContext(5);

        initialize(new SwingBoundMessageProcessor(context));
    }

    AsyncRequest<Void> createAndShowReq() {
        return new AsyncRequest<Void>(getMessageProcessor()) {
            @Override
            public void processRequest() throws Exception {
                //Create and set up the window.
                JFrame frame = new JFrame("HelloWorld");
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); //no exit until all threads are closed.

                //Close context when window is closed.
                frame.addWindowListener((SwingBoundMessageProcessor) getMessageProcessor());

                //Add the "Hello World!" label.
                JLabel label = new JLabel("Hello World!");
                frame.getContentPane().add(label);

                //Display the window.
                frame.pack();
                frame.setVisible(true);

                //return the result.
                processAsyncResponse(null);
            }
        };
    }

}
