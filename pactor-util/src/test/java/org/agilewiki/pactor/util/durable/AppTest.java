package org.agilewiki.pactor.util.durable;

import junit.framework.TestCase;
import org.agilewiki.pactor.api.MailboxFactory;
import org.agilewiki.pactor.util.durable.app.App;
import org.agilewiki.pactor.util.durable.app.AppBase;
import org.agilewiki.pactor.util.durable.app.AppFactory;

public class AppTest extends TestCase {
    public void test1() throws Exception {
        MailboxFactory mailboxFactory = Durables.createMailboxFactory();
        try {
            FactoryLocator factoryLocator = Durables.getFactoryLocator(mailboxFactory);
            User.register(factoryLocator);
            User user1 = (User) Durables.newSerializable(mailboxFactory, "user");
            user1.PAName().setValue("Joe");
            user1.PAAge().setValue(42);
            user1.PALocation().setValue("Boston");

            User user2 = (User) user1.getDurable().copy(null);
            assertEquals("Joe", user2.PAName().getValue());
            assertEquals(42, (int) user2.PAAge().getValue());
            assertEquals("Boston", user2.PALocation().getValue());
        } finally {
            mailboxFactory.close();
        }
    }

    public void test2() throws Exception {
        MailboxFactory mailboxFactory = Durables.createMailboxFactory();
        try {
            FactoryLocator factoryLocator = Durables.getFactoryLocator(mailboxFactory);
            User.register(factoryLocator);
            Box box1 = (Box) Durables.newSerializable(mailboxFactory, Box.FACTORY_NAME);
            box1.setValue("user");
            User user1 = (User) box1.getValue();
            user1.PAName().setValue("Joe");
            user1.PAAge().setValue(42);
            user1.PALocation().setValue("Boston");

            Box box2 = (Box) box1.copy(null);
            User user2 = (User) box2.getValue();
            assertEquals("Joe", user2.PAName().getValue());
            assertEquals(42, (int) user2.PAAge().getValue());
            assertEquals("Boston", user2.PALocation().getValue());
        } finally {
            mailboxFactory.close();
        }
    }
}

class User extends AppBase {
    static void register(final FactoryLocator _factoryLocator) throws Exception {
        _factoryLocator.registerFactory(new AppFactory("user",
                PAString.FACTORY_NAME, PAInteger.FACTORY_NAME, PAString.FACTORY_NAME) {
            @Override
            protected App instantiateActor() {
                return new User();
            }
        });
    }

    static int NAME = 0;
    static int AGE = 1;
    static int LOCATION = 2;

    PAString PAName() throws Exception {
        return (PAString) getDurable()._iGet(NAME);
    }

    PAInteger PAAge() throws Exception {
        return (PAInteger) getDurable()._iGet(AGE);
    }

    PAString PALocation() throws Exception {
        return (PAString) getDurable()._iGet(LOCATION);
    }
}