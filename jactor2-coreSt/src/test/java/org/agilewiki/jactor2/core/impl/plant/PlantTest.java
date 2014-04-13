package org.agilewiki.jactor2.core.impl.plant;

import junit.framework.TestCase;
import org.agilewiki.jactor2.core.impl.Plant;

public class PlantTest extends TestCase {
    public void testa() throws Exception {
        new Plant();
        try {
        } finally {
            Plant.close();
        }
    }
}
