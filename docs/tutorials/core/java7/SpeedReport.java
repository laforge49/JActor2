public class SpeedReport {
    
    public static void print(
            final String _heading, 
            final long _ns, 
            final long _count) {
        System.out.println("");
        System.out.println(_heading);
        System.out.println("Test duration in nanoseconds: " + _ns);
        System.out.println("Number of exchanges: " + _count);
        if (_ns > 0)
            System.out.println("Exchanges per second: " + (1000000000L * _count / _ns));
    }
}
