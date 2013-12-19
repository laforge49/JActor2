package org.agilewiki.jactor2.core.reactors;

import org.agilewiki.jactor2.core.blades.SwingBoundBladeBase;
import org.agilewiki.jactor2.core.messages.AsyncRequest;
import org.agilewiki.jactor2.core.plant.Plant;

import javax.swing.*;

public class SwingBoundReactorSample {
    public static void main(final String[] _args) throws Exception {
        //Create a plant with 5 threads.
        Plant plant = new Plant(5);

        new HelloWorld(new SwingBoundReactor(plant)).createAndShowAReq().signal();
    }
}

class HelloWorld extends SwingBoundBladeBase {
    HelloWorld(final SwingBoundReactor _reactor) throws Exception {
        initialize(_reactor);
    }

    AsyncRequest<Void> createAndShowAReq() {
        return new AsyncBladeRequest<Void>() {
            @Override
            protected void processAsyncRequest() throws Exception {
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
