package org.agilewiki.jactor2.core.reactors;

import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.messages.AsyncRequest;

import javax.swing.*;

public class SwingBoundReactorSample {
    public static void main(final String[] _args) throws Exception {

        new HelloWorld().createAndShowAReq().signal();
    }
}

class HelloWorld extends BladeBase {
    HelloWorld() throws Exception {

        //Create a facility with 5 threads.
        Facility facility = new Facility(5);

        initialize(new SwingBoundReactor(facility));
    }

    AsyncRequest<Void> createAndShowAReq() {
        return new AsyncBladeRequest<Void>() {
            @Override
            protected void processAsyncRequest() throws Exception {
                //Create and set up the window.
                JFrame frame = new JFrame("HelloWorld");
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); //no exit until all threads are closed.

                //Close facility when window is closed.
                frame.addWindowListener((SwingBoundReactor) targetReactor);

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
