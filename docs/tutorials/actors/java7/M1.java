public class M1 {
    public static void main(String[] args) {
        Reply<Void> reply = new Reply<Void>() {
            @Override
            public void response(Void value) {
                System.out.println("end");
            }
        };
        new A1().begin(reply, new B1());
    }
}
