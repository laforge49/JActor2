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
package org.agilewiki.jactor2.core.impl;

import com.blockwithme.util.base.Application;
import com.blockwithme.util.shared.DefaultApplication;

/**
 * @author monster
 *
 */
public class JActorStTestApplication extends DefaultApplication {

    /**
     * Setup test application loop.
     */
    private static void setupApplicationUpdate(final Application app) {
        new com.google.gwt.user.client.Timer() {
            @Override
            public void run() {
                app.run();
            }
        }.scheduleRepeating(50);
    }

    /** Constructor */
    public JActorStTestApplication() {
        try {
            setupApplicationUpdate(this);
        } catch (final/*UnsatisfiedLinkError*/Error e) {
            if (!e.getClass().getSimpleName().equals("UnsatisfiedLinkError")) {
                throw e;
            }
            // GWT Compiler issue; This class is instantiated at compile time,
            // (for whatever reason, by GIN), and at that time,
            // com.google.gwt.user.client.Timer is not available, causing a
            // UnsatisfiedLinkError. But if I catch it as a UnsatisfiedLinkError,
            // then it does not compile because UnsatisfiedLinkError does not
            // exist in GWT! Argh!
        }
    }
}
