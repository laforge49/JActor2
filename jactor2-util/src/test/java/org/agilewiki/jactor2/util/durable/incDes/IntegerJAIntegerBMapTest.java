package org.agilewiki.jactor2.util.durable.incDes;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.util.durable.Durables;

public class IntegerJAIntegerBMapTest extends TestCase {
    public void test1() throws Exception {
        System.out.println(1);
        final Plant plant = Durables.createPlant();
        try {
            final JAMap<Integer, JAInteger> m = (JAMap) Durables
                    .newSerializable(plant, JAMap.INTEGER_JAINTEGER_MAP);
            m.kMake(0);
            m.kMake(1);
            m.kMake(2);
            final JAInteger sj0 = m.kGet(0);
            final JAInteger sj1 = m.kGet(1);
            final JAInteger sj2 = m.kGet(2);
            sj0.setValue(0);
            sj1.setValue(1);
            sj2.setValue(2);
            final Reactor reactor = new NonBlockingReactor(plant);
            final JAMap<Integer, JAInteger> n = (JAMap) m.copy(reactor);
            final JAInteger s0 = n.kGet(0);
            final JAInteger s1 = n.kGet(1);
            final JAInteger s2 = n.kGet(2);
            assertEquals(0, (int) s0.getValue());
            assertEquals(1, (int) s1.getValue());
            assertEquals(2, (int) s2.getValue());
        } finally {
            plant.close();
        }
    }

    public void test2() throws Exception {
        System.out.println(2);
        final Plant plant = Durables.createPlant();
        try {
            final JAMap<Integer, JAInteger> m = (JAMap) Durables
                    .newSerializable(plant, JAMap.INTEGER_JAINTEGER_MAP);
            int i = 0;
            while (i < 28) {
                m.kMake(i);
                final JAInteger ij0 = m.kGet(i);
                ij0.setValue(i);
                i += 1;
            }
            assertEquals(28, m.size());
            i = 0;
            while (i < 28) {
                final JAInteger ij = m.kGet(i);
                assertEquals(i, (int) ij.getValue());
                i += 1;
            }
        } finally {
            plant.close();
        }
    }

    public void test3() throws Exception {
        System.out.println(3);
        final Plant plant = Durables.createPlant();
        try {
            final JAMap<Integer, JAInteger> m = (JAMap) Durables
                    .newSerializable(plant, JAMap.INTEGER_JAINTEGER_MAP);
            int i = 0;
            while (i < 41) {
                m.kMake(i);
                final JAInteger ij0 = m.kGet(i);
                ij0.setValue(i);
                i += 1;
            }
            i = 0;
            while (i < 41) {
                final JAInteger ij = m.kGet(i);
                assertEquals(i, (int) ij.getValue());
                i += 1;
            }
            assertEquals(41, m.size());
        } finally {
            plant.close();
        }
    }

    public void test4() throws Exception {
        System.out.println(4);
        final Plant plant = Durables.createPlant();
        try {
            final JAMap<Integer, JAInteger> m = (JAMap) Durables
                    .newSerializable(plant, JAMap.INTEGER_JAINTEGER_MAP);
            int i = 0;
            while (i < 391) {
                m.kMake(i);
                final JAInteger ij0 = m.kGet(i);
                ij0.setValue(i);
                i += 1;
            }
            assertEquals(391, m.size());
            i = 0;
            while (i < 391) {
                final JAInteger ij = m.kGet(i);
                assertEquals(i, (int) ij.getValue());
                i += 1;
            }
        } finally {
            plant.close();
        }
    }

    public void test5() throws Exception {
        System.out.println(5);
        final Plant plant = Durables.createPlant();
        try {
            final JAMap<Integer, JAInteger> m = (JAMap) Durables
                    .newSerializable(plant, JAMap.INTEGER_JAINTEGER_MAP);
            int i = 0;
            while (i < 10000) {
                m.kMake(i);
                final JAInteger ij0 = m.kGet(i);
                ij0.setValue(i);
                i += 1;
            }
            i = 0;
            while (i < 10000) {
                final JAInteger ij = m.kGet(i);
                assertEquals(i, (int) ij.getValue());
                i += 1;
            }
            assertEquals(10000, m.size());
        } finally {
            plant.close();
        }
    }

    public void test6() throws Exception {
        System.out.println(6);
        final Plant plant = Durables.createPlant();
        try {
            final JAMap<Integer, JAInteger> m = (JAMap) Durables
                    .newSerializable(plant, JAMap.INTEGER_JAINTEGER_MAP);
            int i = 0;
            while (i < 10000) {
                m.kMake(i);
                final JAInteger ij0 = m.kGet(i);
                ij0.setValue(i);
                i += 1;
            }
            i = 0;
            while (i < 10000) {
                m.iRemove(0);
                i += 1;
            }
            assertEquals(0, m.size());
        } finally {
            plant.close();
        }
    }

    public void test7() throws Exception {
        System.out.println(7);
        final Plant plant = Durables.createPlant();
        try {
            final JAMap<Integer, JAInteger> m = (JAMap) Durables
                    .newSerializable(plant, JAMap.INTEGER_JAINTEGER_MAP);
            int i = 0;
            while (i < 10000) {
                m.kMake(i);
                final JAInteger ij0 = m.kGet(i);
                ij0.setValue(i);
                i += 1;
            }
            i = 0;
            while (i < 10000) {
                m.iRemove(-1);
                i += 1;
            }
            assertEquals(0, m.size());
        } finally {
            plant.close();
        }
    }

    public void test8() throws Exception {
        System.out.println(8);
        final Plant plant = Durables.createPlant();
        try {
            final JAMap<Integer, JAInteger> m = (JAMap) Durables
                    .newSerializable(plant, JAMap.INTEGER_JAINTEGER_MAP);
            int i = 0;
            while (i < 10000) {
                m.kMake(i);
                final JAInteger ij0 = m.kGet(i);
                ij0.setValue(i);
                i += 1;
            }
            i = 0;
            while (i < 10000) {
                m.kRemove(i);
                i += 1;
            }
            assertEquals(0, m.size());
        } finally {
            plant.close();
        }
    }

    public void test9() throws Exception {
        System.out.println(9);
        final Plant plant = Durables.createPlant();
        try {
            final JAMap<Integer, JAInteger> m = (JAMap) Durables
                    .newSerializable(plant, JAMap.INTEGER_JAINTEGER_MAP);
            int i = 0;
            while (i < 10000) {
                m.kMake(i);
                final JAInteger ij0 = m.kGet(i);
                ij0.setValue(i);
                i += 1;
            }
            i = 0;
            while (i < 10000) {
                m.kRemove(9999 - i);
                i += 1;
            }
            assertEquals(0, m.size());
        } finally {
            plant.close();
        }
    }
}
