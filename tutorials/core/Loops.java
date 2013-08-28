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
        } finally {
            moduleContext.close();
        }
    }
	
	Sums sums;
	
	Loops(final MessageProcessor _messageProcessor)
            throws Exception {
	    initialize(_messageProcessor);
		ModuleContext moduleContext = _messageProcessor.getModuleContext();
		sums = new Sums(new NonBlockingMessageProcessor(moduleContext));
	}
	
	Request<Integer> loopReq(final int _count) {
        return new Request<Integer>(getMessageProcessor()) {
            @Override
            public void processRequest(final Transport<Integer> _transport) 
                    throws Exception {
            }
        };
	}
}

class Sums extends ActorBase {
    private int total = 0;
	
	Sums(final MessageProcessor _messageProcessor)
            throws Exception {
	    initialize(_messageProcessor);
	}
	
	Request<Integer> addReq(final int _value) {
        return new Request<Integer>(getMessageProcessor()) {
            @Override
            public void processRequest(final Transport<Integer> _transport) 
                    throws Exception {
				total += _value;
                _transport.processResponse(total);
            }
        };
	}
}