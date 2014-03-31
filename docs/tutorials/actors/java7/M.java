public class M {
    public static void main(String[] args) {
        Reply<Void> reply = new Reply<Void>() {
            @Override
            public void response(Void value) {
                System.out.println("end");
            }
        };
        new A().begin(reply, new B());
    }
}
