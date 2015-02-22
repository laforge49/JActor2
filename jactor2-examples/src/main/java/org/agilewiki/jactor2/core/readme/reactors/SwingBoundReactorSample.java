package org.agilewiki.jactor2.core.readme.reactors;

import org.agilewiki.jactor2.core.blades.SwingBoundBladeBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.messages.AIOp;
import org.agilewiki.jactor2.core.messages.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messages.impl.AsyncRequestImpl;
import org.agilewiki.jactor2.core.reactors.SwingBoundReactor;

import javax.swing.*;

public class SwingBoundReactorSample {
    public static void main(final String[] _args) throws Exception {
        //Create a plant with 5 threads.
        Plant plant = new Plant(5);

        new HelloWorld(new SwingBoundReactor()).createAndShowAOp().signal();
    }
}

class HelloWorld extends SwingBoundBladeBase {
    HelloWorld(final SwingBoundReactor _reactor) throws Exception {
        super(_reactor);
    }

    AIOp<Void> createAndShowAOp() {
        return new AIOp<Void>("createAndShow", getReactor()) {
            @Override
            protected void processAsyncOperation(AsyncRequestImpl _asyncRequestImpl,
                                              AsyncResponseProcessor<Void> _asyncResponseProcessor)
                    throws Exception {
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
                _asyncResponseProcessor.processAsyncResponse(null);
            }
        };
    }

}
