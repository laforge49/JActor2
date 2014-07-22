import org.agilewiki.jactor2.core.impl.Plant;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.closeable.Closeable;
import org.agilewiki.jactor2.core.closeable.impl.CloseableImpl;
import org.agilewiki.jactor2.core.closeable.impl.CloseableImplImpl;

public class SampleCloseable implements Closeable {
    public static void main(final String[] _args) throws Exception {
        new Plant();
        try {
            System.out.println("\n   test 1");
            NonBlockingReactor r1 = new NonBlockingReactor();
            Closeable c1 = new SampleCloseable();
            r1.addCloseable(c1);
            r1.close();
            
            System.out.println("\n   test 2");
            NonBlockingReactor r2 = new NonBlockingReactor();
            Closeable c2 = new SampleCloseable();
            r2.addCloseable(c2);
        } finally {
            Plant.close();
        }
    }
    
    private final CloseableImpl closeableImpl;
    
    public SampleCloseable() {
        closeableImpl = new CloseableImplImpl(this);
    }
    
    @Override
    public void close() throws Exception {
        System.out.println("sample closed");
        closeableImpl.close();
    }
    
    @Override
    public CloseableImpl asCloseableImpl() {
        return closeableImpl;
    }
}
