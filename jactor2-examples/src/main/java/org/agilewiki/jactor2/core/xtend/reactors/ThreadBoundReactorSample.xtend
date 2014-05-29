package org.agilewiki.jactor2.core.xtend.reactors;

import org.agilewiki.jactor2.core.blades.ThreadBoundBladeBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.reactors.ThreadBoundReactor;
import org.agilewiki.jactor2.core.requests.SyncRequest;

class ThreadBoundReactorSample {

    def static void main(String[] args) throws Exception {

        //A plant with no threads.
        new Plant(0);

        //Get a reference to the main thread.
        val mainThread = Thread.currentThread();

        //Create a thread-bound processing.
        val reactor = new ThreadBoundReactor(
                new Runnable() {
                    override void run() {
                        //Interrupt the main thread when there are messages to process
                        mainThread.interrupt();
                    }
                });

        //Create an blades that uses the thread-bound processing.
        val threadBoundBlade = new SampleThreadBoundBlade(reactor);

        //Terminate the blades.
        new SyncRequest<Void>(threadBoundBlade.getReactor()) {

            override Void processSyncRequest() throws Exception {
                threadBoundBlade.fin();
                return null;
            }
        }.signal();

        //Process messages when this thread is interrupted.
        while (true) {
            try {
                //Wait for an interrupt.
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                //Process messages when the main thread is interrupted.
                reactor.run();
            }
        }
    }
}

class SampleThreadBoundBlade extends ThreadBoundBladeBase {

    new(ThreadBoundReactor _reactor) throws Exception {
        super(_reactor);
    }

    //Print "finished" and exit when fin is called.
    def void fin() throws Exception {
        System.out.println("finished");
        System.exit(0);
    }
}
