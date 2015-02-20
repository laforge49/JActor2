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
package org.agilewiki.jactor2.core.messages.impl;

/**
 * API for internal payload-carrying request implementations.
 *
 * The goal of this interface is to define a RequestImpl that can be used
 * directly, without requiring *one additional object* creation *per request*.
 * The "logic" of the request is already defined in an Operation, and in many
 * cases, this logic can be implemented as a singleton. But if the Operation
 * needs "variables" to operate it's work, a new instance of the Operation must
 * be created with every request. By having a pre-defined native request that
 * can carry a small amount of "user payload" with itself (the only use-case
 * for which this Request makes sense), we should be able to make most
 * Operations into singletons.
 *
 * Most method calls don't have more then 3 parameters, so we currently
 * support only 3 parameters (index 0 to 2), but since they could be either
 * Objects or primitive values, we support both double and Object as
 * parameters, such that we have in effect 6 parameters. Please remember that
 * *all* primitive values can be *safely* stored in a double, *except long*.
 *
 * @param <RESPONSE_TYPE>    The return value type.
 *
 * @author monster
 */
public interface RequestImplWithData<RESPONSE_TYPE> extends
        RequestImpl<RESPONSE_TYPE> {
    /**
     * Return a double parameter.
     *
     * @param index The index of the double parameter, in [0,2]
     *
     * @return The double parameter.
     */
    double getDouble(int index);

    /**
     * Sets a double parameter.
     *
     * @param index The index of the double parameter, in [0,2]
     * @param newValue The new value of the double parameter.
     */
    void setDouble(int index, double newValue);

    /**
     * Return an Object parameter.
     *
     * @param index The index of the Object parameter, in [0,2]
     *
     * @return The Object parameter.
     */
    Object getObject(int index);

    /**
     * Sets an Object parameter.
     *
     * @param index The index of the Object parameter, in [0,2]
     * @param newValue The new value of the Object parameter.
     */
    void setObject(int index, Object newValue);
}
