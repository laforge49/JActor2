import org.agilewiki.jactor2.core.blades.NonBlockingBladeBase;
import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.requests.AOp;
import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.requests.SOp;
import org.agilewiki.jactor2.core.reactors.BlockingReactor;
import org.agilewiki.jactor2.core.requests.impl.AsyncRequestImpl;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

public class DiningRoom extends NonBlockingBladeBase {
	public DiningRoom() throws Exception {
	}

    public AOp<List<Integer>> feastAOp(final int _seats, final int _meals) {
        return new AOp<List<Integer>>("feast", getReactor()) {
            List<Integer> mealsEaten = new LinkedList<Integer>();
            
            @Override
            protected void processAsyncOperation(final AsyncRequestImpl _asyncRequestImpl, 
					final AsyncResponseProcessor<List<Integer>> _asyncResponseProcessor) throws Exception {

				AsyncResponseProcessor<Integer> feastResponseProcessor =
						new AsyncResponseProcessor<Integer>() {
					@Override
					public void processAsyncResponse(final Integer _feastResponse) throws Exception {
						mealsEaten.add(_feastResponse);
						if (mealsEaten.size() == _seats) {
							_asyncResponseProcessor.processAsyncResponse(mealsEaten);
						}
					}
				};
            
                int i = 0;
                final DiningTable diningTable = new DiningTable(
                    _seats,
                    _meals);
                while (i < _seats) {
                    DiningPhilosopher diningPhilosopher =
                        new DiningPhilosopher();
                    AOp<Integer> feastAReq = diningPhilosopher.feastAOp(diningTable, i);
                    _asyncRequestImpl.send(feastAReq, feastResponseProcessor);
                    ++i;
                }
            }
        };
    }
    
    public static void main(String[] args) throws Exception {
        int seats = 5;
        int meals = 1000000;
        new Plant();
        try {
            DiningRoom diningRoom = new DiningRoom();
            AOp<List<Integer>> feastAOp = diningRoom.feastAOp(seats, meals);
            long before = System.nanoTime();
            List<Integer> mealsEaten = feastAOp.call();
            long after = System.nanoTime();
            long duration = after - before;
            System.out.println("Seats: " + seats);
            System.out.println("Meals: " + meals);
            System.out.println("\nMeals eaten by each philosopher:");
            Iterator<Integer> it = mealsEaten.iterator();
            int totalEaten = 0;
            while (it.hasNext()) {
                int me = it.next();
                totalEaten += me;
                if (mealsEaten.size() < 11)
                    System.out.println("    " + me);
            }
            if (totalEaten != meals)
                throw new IllegalStateException("total meals eaten does not match: " + totalEaten);
            System.out.println("\nTest duration in nanoseconds: " + duration);
            if (duration > 0) {
                System.out.println("Total meals eaten per second: " + (1000000000L * meals / duration));
            }
        } finally {
            Plant.close();
        }
    }
}
