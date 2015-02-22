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
package org.agilewiki.jactor2.core.impl.mtMessages;

import org.agilewiki.jactor2.core.messages.SyncOperation;
import org.agilewiki.jactor2.core.messages.impl.RequestImplWithData;
import org.agilewiki.jactor2.core.reactors.Reactor;

/**
 * Internal implementation of a SyncRequest, with user payload.
 *
 * @param <RESPONSE_TYPE> The response value type.
 *
 * @author monster
 */
public class SyncRequestMtImplWithData<RESPONSE_TYPE> extends
        SyncRequestMtImpl<RESPONSE_TYPE> implements
        RequestImplWithData<RESPONSE_TYPE> {

    /** Double parameter 0. */
    private double doubleParam0;

    /** Double parameter 1. */
    private double doubleParam1;

    /** Double parameter 2. */
    private double doubleParam2;

    /** Object parameter 0. */
    private Object objectParam0;

    /** Object parameter 1. */
    private Object objectParam1;

    /** Object parameter 2. */
    private Object objectParam2;

    /**
     * @param _syncOperation
     * @param _targetReactor
     */
    public SyncRequestMtImplWithData(
            final SyncOperation<RESPONSE_TYPE> _syncOperation,
            final Reactor _targetReactor) {
        super(_syncOperation, _targetReactor);
    }

    /**
     * @param _targetReactor
     */
    public SyncRequestMtImplWithData(final Reactor _targetReactor) {
        super(_targetReactor);
    }

    /** {@inheritDoc} */
    @Override
    public double getDouble(final int index) {
        if (index == 0) {
            return doubleParam0;
        }
        if (index == 1) {
            return doubleParam1;
        }
        // We assume the use of invalid indexes would have already failed when
        // calling the setter.
        return doubleParam2;
    }

    /** {@inheritDoc} */
    @Override
    public void setDouble(final int index, final double newValue) {
        if (index == 0) {
            doubleParam0 = newValue;
        } else if (index == 1) {
            doubleParam1 = newValue;
        } else if (index == 2) {
            doubleParam2 = newValue;
        } else {
            throw new IndexOutOfBoundsException(String.valueOf(index));
        }
    }

    /** {@inheritDoc} */
    @Override
    public Object getObject(final int index) {
        if (index == 0) {
            return objectParam0;
        }
        if (index == 1) {
            return objectParam1;
        }
        // We assume the use of invalid indexes would have already failed when
        // calling the setter.
        return objectParam2;
    }

    /** {@inheritDoc} */
    @Override
    public void setObject(final int index, final Object newValue) {
        if (index == 0) {
            objectParam0 = newValue;
        } else if (index == 1) {
            objectParam1 = newValue;
        } else if (index == 2) {
            objectParam2 = newValue;
        } else {
            throw new IndexOutOfBoundsException(String.valueOf(index));
        }
    }
}
