package org.agilewiki.jactor2.core.reactors;

import org.agilewiki.jactor2.core.blades.BladeBase;
import org.agilewiki.jactor2.core.facilities.Plant;
import org.agilewiki.jactor2.core.messages.Event;

public class ThreadBoundReactorSample {

    public static void main(String[] args) throws Exception {

        //A plant with no threads.
        final Plant plant = new Plant(0);

        //Get a reference to the main thread.
        final Thread mainThread = Thread.currentThread();

        //Create a thread-bound processing.
        final ThreadBoundReactor boundMessageProcessor =
                new ThreadBoundReactor(plant, new Runnable() {
                    @Override
                    public void run() {
                        //Interrupt the main thread when there are messages to process
                        mainThread.interrupt();
                    }
                });

        //Create an blade that uses the thread-bound processing.
        final ThreadBoundBlade threadBoundBlade = new ThreadBoundBlade(boundMessageProcessor);

        //Pass a FinEvent signal to the blade.
        new FinEvent().signal(threadBoundBlade);

        //Process messages when this thread is interrupted.
        while (true) {
            try {
                //Wait for an interrupt.
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                //Process messages when the main thread is interrupted.
                boundMessageProcessor.run();
            }
        }
    }
}

class ThreadBoundBlade extends BladeBase {

    ThreadBoundBlade(final Reactor _reactor) throws Exception {
        initialize(_reactor);
    }

    //Print "finished" and exit when fin is called.
    void fin() throws Exception {
        System.out.println("finished");
        System.exit(0);
    }
}

//When a FinEvent is passed to an blade, the fin method is called.
class FinEvent extends Event<ThreadBoundBlade> {
    @Override
    protected void processEvent(ThreadBoundBlade _targetBlade) throws Exception {
        _targetBlade.fin();
    }
}
