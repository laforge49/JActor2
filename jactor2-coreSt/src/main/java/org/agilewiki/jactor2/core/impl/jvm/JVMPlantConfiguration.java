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
package org.agilewiki.jactor2.core.impl.jvm;

import org.agilewiki.jactor2.core.impl.stPlant.PlantConfiguration;
import org.agilewiki.jactor2.core.plant.PlantScheduler;
import org.agilewiki.jactor2.core.util.GwtIncompatible;

/**
 * The JVM (non-GWT) single-threaded PlantConfiguration implementation.
 *
 * @author monster
 */
@GwtIncompatible
public class JVMPlantConfiguration extends PlantConfiguration {

    /* (non-Javadoc)
     * @see org.agilewiki.jactor2.core.impl.stPlant.PlantConfiguration#createPlantScheduler()
     */
    @Override
    protected PlantScheduler createPlantScheduler() {
        return new JVMPlantScheduler();
    }
}
