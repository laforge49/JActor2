package org.agilewiki.jactor2.util.durable.incDes;

import junit.framework.TestCase;

import org.agilewiki.jactor2.core.facilities.Plant;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.util.durable.Durables;

public class BListTest extends TestCase {
    public void test1() throws Exception {
        System.out.println("1");
        final Plant plant = Durables.createPlant();
        try {
            final JAList<JAString> stringList1 = (JAList) Durables
                    .newSerializable(plant, JAList.JASTRING_LIST);
            stringList1.iAdd(0);
            stringList1.iAdd(1);
            stringList1.iAdd(2);
            final JAString sj0 = stringList1.iGet(0);
            final JAString sj1 = stringList1.iGet(1);
            final JAString sj2 = stringList1.iGet(2);
            sj0.setValue("a");
            sj1.setValue("b");
            sj2.setValue("c");
            final Reactor reactor = new NonBlockingReactor(plant);
            final JAList<JAString> stringList2 = (JAList) stringList1
                    .copy(reactor);
            final JAString s0 = stringList2.iGet(0);
            final JAString s1 = stringList2.iGet(1);
            final JAString s2 = stringList2.iGet(2);
            assertEquals("a", s0.getValue());
            assertEquals("b", s1.getValue());
            assertEquals("c", s2.getValue());
        } finally {
            plant.close();
        }
    }

    public void test2() throws Exception {
        System.out.println("2");
        final Plant plant = Durables.createPlant();
        try {
            final JAList<JAInteger> intList1 = (JAList) Durables
                    .newSerializable(plant, JAList.JAINTEGER_LIST);
            int i = 0;
            while (i < 28) {
                intList1.iAdd(i);
                final JAInteger ij0 = intList1.iGet(i);
                ij0.setValue(i);
                i += 1;
            }
            i = 0;
            while (i < 28) {
                final JAInteger ij = intList1.iGet(i);
                assertEquals(i, (int) ij.getValue());
                i += 1;
            }
        } finally {
            plant.close();
        }
    }

    public void test3() throws Exception {
        System.out.println("3");
        final Plant plant = Durables.createPlant();
        try {
            final JAList<JAInteger> intList1 = (JAList) Durables
                    .newSerializable(plant, JAList.JAINTEGER_LIST);
            int i = 0;
            while (i < 41) {
                intList1.iAdd(-1);
                final JAInteger ij0 = intList1.iGet(-1);
                ij0.setValue(i);
                i += 1;
            }
            i = 0;
            while (i < 41) {
                final JAInteger ij = intList1.iGet(i);
                assertEquals(i, (int) ij.getValue());
                i += 1;
            }
        } finally {
            plant.close();
        }
    }

    public void test4() throws Exception {
        System.out.println("4");
        final Plant plant = Durables.createPlant();
        try {
            final JAList<JAInteger> intList1 = (JAList) Durables
                    .newSerializable(plant, JAList.JAINTEGER_LIST);
            int i = 0;
            while (i < 391) {
                intList1.iAdd(-1);
                final JAInteger ij0 = intList1.iGet(-1);
                ij0.setValue(i);
                i += 1;
            }
            i = 0;
            while (i < 391) {
                final JAInteger ij = intList1.iGet(i);
                assertEquals(i, (int) ij.getValue());
                i += 1;
            }
        } finally {
            plant.close();
        }
    }

    public void test5() throws Exception {
        System.out.println("5");
        final Plant plant = Durables.createPlant();
        try {
            final JAList<JAInteger> intList1 = (JAList) Durables
                    .newSerializable(plant, JAList.JAINTEGER_LIST);
            int i = 0;
            while (i < 10000) {
                intList1.iAdd(-1);
                final JAInteger ij0 = intList1.iGet(-1);
                ij0.setValue(i);
                i += 1;
            }
            i = 0;
            while (i < 10000) {
                final JAInteger ij = intList1.iGet(i);
                assertEquals(i, (int) ij.getValue());
                i += 1;
            }
        } finally {
            plant.close();
        }
    }

    public void test6() throws Exception {
        System.out.println("6");
        final Plant plant = Durables.createPlant();
        try {
            final JAList<JAInteger> intList1 = (JAList) Durables
                    .newSerializable(plant, JAList.JAINTEGER_LIST);
            int i = 0;
            while (i < 10000) {
                intList1.iAdd(-1);
                final JAInteger ij0 = intList1.iGet(-1);
                ij0.setValue(i);
                i += 1;
            }
            i = 0;
            while (i < 10000) {
                intList1.iRemove(-1);
                i += 1;
            }
            assertEquals(0, intList1.size());
        } finally {
            plant.close();
        }
    }
}
