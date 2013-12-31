package org.agilewiki.jactor2.util.durable.app;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.util.durable.Durables;
import org.agilewiki.jactor2.util.durable.FactoryLocator;
import org.agilewiki.jactor2.util.durable.incDes.Box;

public class AppTest extends TestCase {
    public void test1() throws Exception {
        final Plant plant = Durables.createPlant();
        try {
            final FactoryLocator factoryLocator = Durables
                    .getFactoryLocator(plant);
            User.register(factoryLocator);
            final User user1 = (User) Durables.newSerializable(plant, "user");
            user1.PAName().setValue("Joe");
            user1.PAAge().setValue(42);
            user1.PALocation().setValue("Boston");

            final User user2 = (User) user1.getDurable().copy(null);
            Assert.assertEquals("Joe", user2.PAName().getValue());
            assertEquals(42, (int) user2.PAAge().getValue());
            Assert.assertEquals("Boston", user2.PALocation().getValue());
        } finally {
            plant.close();
        }
    }

    public void test2() throws Exception {
        final Plant plant = Durables.createPlant();
        try {
            final FactoryLocator factoryLocator = Durables
                    .getFactoryLocator(plant);
            User.register(factoryLocator);
            final Box box1 = (Box) Durables.newSerializable(plant,
                    Box.FACTORY_NAME);
            box1.setValue("user");
            final User user1 = (User) box1.getValue();
            user1.PAName().setValue("Joe");
            user1.PAAge().setValue(42);
            user1.PALocation().setValue("Boston");

            final Box box2 = (Box) box1.copy(null);
            final User user2 = (User) box2.getValue();
            Assert.assertEquals("Joe", user2.PAName().getValue());
            assertEquals(42, (int) user2.PAAge().getValue());
            Assert.assertEquals("Boston", user2.PALocation().getValue());
        } finally {
            plant.close();
        }
    }
}
