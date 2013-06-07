package org.agilewiki.jactor.util.fbp;

import java.util.concurrent.ArrayBlockingQueue;

public class Flow implements AutoCloseable {

    private ArrayBlockingQueue queue;

    private InPort inPort;

    private OutPort outPort;

    private FActor source;

    private FActor target;

    public Flow(final FActor _source, final FActor _target) {
        this(1, _source, _target);
    }

    public Flow(int i, final FActor _source, final FActor _target) {
        source = _source;
        target = _target;
        queue = new ArrayBlockingQueue(i);
        inPort = new InPort() {

            @Override
            public void run() {
                try {
                    Object e = queue.take();
                    target.gotNext(this, e);
                } catch(InterruptedException ie) {
                }
            }

            @Override
            public FActor getSource() {
                return source;
            }

            @Override
            public void close() throws Exception {
                Flow.this.close();
            }
        };
        outPort = new OutPort() {

            private Object e;

            @Override
            public void run() {
                try {
                    queue.put(e);
                    target.wrote(this);
                } catch (InterruptedException e1) {
                }
            }

            @Override
            public FActor getTarget() {
                return target;
            }

            @Override
            public void write(Object _e) {
                e = _e;
            }

            @Override
            public void close() throws Exception {
                Flow.this.close();
            }
        };
        target.addInPort(inPort);
        source.addOutPort(outPort);
    }

    public InPort getInPort() {
        return inPort;
    }

    public OutPort getOutPort() {
        return outPort;
    }

    @Override
    public void close() throws Exception {
        try {
            source.closed(outPort);
        } catch (Exception se) {
        }
        try {
            target.closed(inPort);
        } catch (Exception se) {
        }
    }
}
