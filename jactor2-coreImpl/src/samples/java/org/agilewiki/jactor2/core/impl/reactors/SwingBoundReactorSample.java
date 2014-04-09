package org.agilewiki.jactor2.core.impl.reactors;

import org.agilewiki.jactor2.core.blades.SwingBoundBladeBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.reactors.SwingBoundReactor;
import org.agilewiki.jactor2.core.requests.AsyncRequest;

import javax.swing.*;

public class SwingBoundReactorSample {
    public static void main(final String[] _args) throws Exception {
        //Create a plant with 5 threads.
        Plant plant = new Plant(5);

        new HelloWorld(new SwingBoundReactor()).createAndShowAReq().signal();
    }
}

class HelloWorld extends SwingBoundBladeBase {
    HelloWorld(final SwingBoundReactor _reactor) throws Exception {
        super(_reactor);
    }

    AsyncRequest<Void> createAndShowAReq() {
        return new AsyncBladeRequest<Void>() {
            @Override
            public void processAsyncRequest() {
                //Create and set up the window.
                JFrame frame = new JFrame("HelloWorld");
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); //no exit until all threads are closed.

                //Close plant when window is closed.
                frame.addWindowListener((SwingBoundReactor) getReactor());

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
