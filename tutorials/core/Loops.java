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
			/*
			System.out.println("\nshared message processor tests:");
            sums = new Sums(loops.getMessageProcessor());
			test(1000000000, sums, loops);
		    sums.getMessageProcessor().getModuleContext().close();
			
			System.out.println("\nno thread migration tests:");
            sums = new Sums(new NonBlockingMessageProcessor(new ModuleContext()));
			test(10000000, sums, loops);
		    sums.getMessageProcessor().getModuleContext().close();
			*/
			System.out.println("\nthread migration tests:");
            sums = new Sums(new NonBlockingMessageProcessor(moduleContext));
			test(10000000, sums, loops);
			Thread.sleep(10);
			
        } finally {
            moduleContext.close();
        }
    }
	
	static void test(final long _c, final Sums _sums, final Loops loops) throws Exception {
			/*
			System.out.println("1 => " + loops.loopReq(_sums, 1).call());
		    _sums.clearReq().signal();
			System.out.println("2 => " + loops.loopReq(_sums, 2).call());
		    _sums.clearReq().signal();
			System.out.println("100 => " + loops.loopReq(_sums, 100).call());
		    _sums.clearReq().signal();
			*/
			System.gc();
			long t0 = System.currentTimeMillis();
			long r = loops.loopReq(_sums, _c).call();
			long t1 = System.currentTimeMillis();
			long d = t1 - t0;
			System.out.println("" + _c + " => " + r + " in " + d + " milliseconds");
			if (d > 0)
			    System.out.println("" + (_c * 1000 / d) + " requests/responses per second");
		    _sums.clearReq().signal();
	}
	
	Loops(final MessageProcessor _messageProcessor)
            throws Exception {
	    initialize(_messageProcessor);
	}
	
	Request<Long> loopReq(final Sums _sums, final long _count) {
        return new Request<Long>(getMessageProcessor()) {
		    long counter;
			Transport<Long> transport;
			ResponseProcessor<Long> responseProcessor = new ResponseProcessor<Long>() {
			    @Override
				public void processResponse(final Long _response) throws Exception {
				    if (counter == 1) {
					    transport.processResponse(_response);
					} else {
					    counter -= 1;
        			    _sums.addReq(counter).send(getMessageProcessor(), responseProcessor);
					}
				}
			};
			
            @Override
            public void processRequest(final Transport<Long> _transport) 
                    throws Exception {
				if (_count < 1) {
				    _transport.processResponse(0L);
				} else {
				    counter = _count;
					transport = _transport;
    			    _sums.addReq(counter).send(getMessageProcessor(), responseProcessor);
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
	
	Request<Void> clearReq() {
        return new Request<Void>(getMessageProcessor()) {
            @Override
            public void processRequest(final Transport<Void> _transport) 
                    throws Exception {
				total = 0L;
                _transport.processResponse(null);
            }
        };
	}
	
	Request<Long> addReq(final long _value) {
        return new Request<Long>(getMessageProcessor()) {
            @Override
            public void processRequest(final Transport<Long> _transport) 
                    throws Exception {
				total += _value;
                _transport.processResponse(total);
            }
        };
	}
}