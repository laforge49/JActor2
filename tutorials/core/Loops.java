import org.agilewiki.jactor2.core.*;
import org.agilewiki.jactor2.core.threading.*;
import org.agilewiki.jactor2.core.messaging.*;
import org.agilewiki.jactor2.core.processing.*;

public class Loops extends ActorBase {
    public static void main(final String[] _args) 
            throws Exception {
        ModuleContext moduleContext = new ModuleContext();
        try {
            Loops loops = new Loops(new NonBlockingMessageProcessor(moduleContext));
            Sums sums;
            
            System.out.println("\nshared message processor tests:");
            sums = new Sums(loops.getMessageProcessor());
            test(10000000, sums, loops);
            sums.getMessageProcessor().getModuleContext().close();
            /*
            System.out.println("\nno thread migration tests:");
            sums = new Sums(new NonBlockingMessageProcessor(new ModuleContext()));
            test(10000000, sums, loops);
            sums.getMessageProcessor().getModuleContext().close();
            
            System.out.println("\nthread migration tests:");
            sums = new Sums(new NonBlockingMessageProcessor(moduleContext));
            test(10000000, sums, loops);
            Thread.sleep(10);
            */
        } finally {
            moduleContext.close();
        }
    }
    
    static void test(final long _c, final Sums _sums, final Loops loops) throws Exception {
            /*
            System.out.println("1 => " + loops.loopReq(_sums, 1).call());
            _sums.clearAReq().signal();
            System.out.println("2 => " + loops.loopReq(_sums, 2).call());
            _sums.clearAReq().signal();
            System.out.println("100 => " + loops.loopReq(_sums, 100).call());
            _sums.clearAReq().signal();
            */
            System.gc();
            long t0 = System.currentTimeMillis();
            long r = loops.loopAReq(_sums, _c).call();
            long t1 = System.currentTimeMillis();
            long d = t1 - t0;
            System.out.println("" + _c + " => " + r + " in " + d + " milliseconds");
            if (d > 0)
                System.out.println("" + (_c * 1000 / d) + " requests/responses per second");
            _sums.clearAReq().signal();
    }
    
    Loops(final MessageProcessor _messageProcessor)
            throws Exception {
        initialize(_messageProcessor);
    }
    
    AsyncRequest<Long> loopAReq(final Sums _sums, final long _count) {
        return new AsyncRequest<Long>(getMessageProcessor()) {
            long counter;
            AsyncResponseProcessor dis = this;
            
            AsyncResponseProcessor<Long> responseProcessor = new AsyncResponseProcessor<Long>() {
                @Override
                public void processAsyncResponse(final Long _response) throws Exception {
                    if (counter == 1) {
                        dis.processAsyncResponse(_response);
                    } else {
                        counter -= 1;
                        _sums.addAReq(counter).send(getMessageProcessor(), responseProcessor);
                    }
                }
            };
            
            @Override
            public void processAsyncRequest() 
                    throws Exception {
                if (_count < 1) {
                    processAsyncResponse(0L);
                } else {
                    counter = _count;
                    _sums.addAReq(counter).send(getMessageProcessor(), responseProcessor);
                }
            }
        };
    }
}

class Sums extends ActorBase {
    private long total = 0;
    
    Sums(final MessageProcessor _messageProcessor)
            throws Exception {
        initialize(_messageProcessor);
    }
    
    AsyncRequest<Void> clearAReq() {
        return new AsyncRequest<Void>(getMessageProcessor()) {
            @Override
            public void processAsyncRequest() 
                    throws Exception {
                total = 0L;
                processAsyncResponse(null);
            }
        };
    }
    
    AsyncRequest<Long> addAReq(final long _value) {
        return new AsyncRequest<Long>(getMessageProcessor()) {
            @Override
            public void processAsyncRequest() 
                    throws Exception {
                total += _value;
                processAsyncResponse(total);
            }
        };
    }
}
