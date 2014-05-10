/*
 * Copyright (C) 2014 Sebastien Diot.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.agilewiki.jactor2.core.impl.plant;

import org.agilewiki.jactor2.core.impl.JActorStTestInjector;

import com.blockwithme.util.base.SystemUtils;
import com.blockwithme.util.client.UtilEntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;

/**
 * Base class for our tests.
 *
 * @author monster
 */
public abstract class BaseGWTTestCase extends GWTTestCase {

    /** The injector */
    private static JActorStTestInjector injector;

    @Override
    protected void gwtSetUp() throws Exception {
        if (injector == null) {
            new UtilEntryPoint().onModuleLoad();
            injector = GWT.create(JActorStTestInjector.class);
            assertNotNull("TestInjector", injector);
        }
    }

    @Override
    protected void gwtTearDown() throws Exception {
        // Just in case, we don't get back to the event loop between
        // test methods
        SystemUtils.updateCurrentTimeMillis();
    }

    /* (non-Javadoc)
     * @see com.google.gwt.junit.client.GWTTestCase#getModuleName()
     */
    @Override
    public String getModuleName() {
        return "org.agilewiki.jactor2.core.JActor2CoreSt";
    }
}
