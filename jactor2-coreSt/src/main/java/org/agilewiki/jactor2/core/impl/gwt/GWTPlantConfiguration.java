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
package org.agilewiki.jactor2.core.impl.gwt;

import org.agilewiki.jactor2.core.impl.stPlant.PlantConfiguration;
import org.agilewiki.jactor2.core.plant.PlantScheduler;

/**
 * Implementation of PlantConfiguration for GWT.
 *
 * @author monster
 */
public class GWTPlantConfiguration extends PlantConfiguration {

    /* (non-Javadoc)
     * @see org.agilewiki.jactor2.core.impl.stPlant.PlantConfiguration#createPlantScheduler()
     */
    @Override
    protected PlantScheduler createPlantScheduler() {
        return new GWTPlantScheduler();
    }

    /* (non-Javadoc)
     * @see org.agilewiki.jactor2.core.impl.stPlant.PlantConfiguration#asString(java.lang.String, java.lang.Throwable)
     */
    @Override
    protected String asString(final String msg, final Throwable throwable) {
        return msg + "\n" + getMessage(throwable);
    }

    /** Recursively converts a Throwable to a String. Result may vary depending on compilation mode and browser. */
    private static String getMessage(Throwable throwable) {
        String ret = "";
        while (throwable != null) {
            if (throwable instanceof com.google.gwt.event.shared.UmbrellaException) {
                for (final Throwable thr2 : ((com.google.gwt.event.shared.UmbrellaException) throwable)
                        .getCauses()) {
                    if (ret != "")
                        ret += "\nCaused by: ";
                    ret += thr2.toString();
                    ret += "\n  at " + getMessage(thr2);
                }
            } else if (throwable instanceof com.google.web.bindery.event.shared.UmbrellaException) {
                for (final Throwable thr2 : ((com.google.web.bindery.event.shared.UmbrellaException) throwable)
                        .getCauses()) {
                    if (ret != "")
                        ret += "\nCaused by: ";
                    ret += thr2.toString();
                    ret += "\n  at " + getMessage(thr2);
                }
            } else {
                if (ret != "")
                    ret += "\nCaused by: ";
                ret += throwable.toString();
                for (final StackTraceElement sTE : throwable.getStackTrace())
                    ret += "\n  at " + sTE;
            }
            throwable = throwable.getCause();
        }

        return ret;
    }
}
