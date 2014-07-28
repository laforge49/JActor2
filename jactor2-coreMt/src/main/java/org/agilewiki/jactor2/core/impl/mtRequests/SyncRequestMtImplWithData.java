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
package org.agilewiki.jactor2.core.impl.mtRequests;

import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.requests.SyncNativeRequestWithData;
import org.agilewiki.jactor2.core.requests.SyncOperation;

/**
 * Internal implementation of a SyncRequest, with user payload.
 *
 * @param <RESPONSE_TYPE> The response value type.
 *
 * @author monster
 */
public class SyncRequestMtImplWithData<RESPONSE_TYPE> extends
        SyncRequestMtImpl<RESPONSE_TYPE> implements
        SyncNativeRequestWithData<RESPONSE_TYPE> {

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
    public double getDouble0() {
        return doubleParam0;
    }

    /** {@inheritDoc} */
    @Override
    public void setDouble0(final double newValue) {
        doubleParam0 = newValue;
    }

    /** {@inheritDoc} */
    @Override
    public double getDouble1() {
        return doubleParam1;
    }

    /** {@inheritDoc} */
    @Override
    public void setDouble1(final double newValue) {
        doubleParam1 = newValue;
    }

    /** {@inheritDoc} */
    @Override
    public double getDouble2() {
        return doubleParam2;
    }

    /** {@inheritDoc} */
    @Override
    public void setDouble2(final double newValue) {
        doubleParam2 = newValue;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public Object getObject0() {
        return objectParam0;
    }

    /** {@inheritDoc} */
    @Override
    public void setObject0(final Object newValue) {
        objectParam0 = newValue;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public Object getObject1() {
        return objectParam1;
    }

    /** {@inheritDoc} */
    @Override
    public void setObject1(final Object newValue) {
        objectParam1 = newValue;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public Object getObject2() {
        return objectParam2;
    }

    /** {@inheritDoc} */
    @Override
    public void setObject2(final Object newValue) {
        objectParam2 = newValue;
    }

    /** {@inheritDoc} */
    @Override
    public boolean getBoolean0() {
        return doubleParam0 != 0;
    }

    /** {@inheritDoc} */
    @Override
    public void setBoolean0(final boolean newValue) {
        doubleParam0 = newValue ? 1 : 0;
    }

    /** {@inheritDoc} */
    @Override
    public byte getByte0() {
        return (byte) doubleParam0;
    }

    /** {@inheritDoc} */
    @Override
    public void setByte0(final byte newValue) {
        doubleParam0 = newValue;
    }

    /** {@inheritDoc} */
    @Override
    public char getChar0() {
        return (char) doubleParam0;
    }

    /** {@inheritDoc} */
    @Override
    public void setChar0(final char newValue) {
        doubleParam0 = newValue;
    }

    /** {@inheritDoc} */
    @Override
    public short getShort0() {
        return (short) doubleParam0;
    }

    /** {@inheritDoc} */
    @Override
    public void setShort0(final short newValue) {
        doubleParam0 = newValue;
    }

    /** {@inheritDoc} */
    @Override
    public int getInt0() {
        return (int) doubleParam0;
    }

    /** {@inheritDoc} */
    @Override
    public void setInt0(final int newValue) {
        doubleParam0 = newValue;
    }

    /** {@inheritDoc} */
    @Override
    public float getFloat0() {
        return (float) doubleParam0;
    }

    /** {@inheritDoc} */
    @Override
    public void setFloat0(final float newValue) {
        doubleParam0 = newValue;
    }

    /** {@inheritDoc} */
    @Override
    public long getLong0() {
        if (objectParam0 instanceof Long) {
            return (Long) objectParam0;
        }
        return (long) doubleParam0;
    }

    /** {@inheritDoc} */
    @Override
    public void setLong0(final long newValue) {
        if ((newValue >= MIN_LONG_VALUE) && (newValue <= MAX_LONG_VALUE)) {
            doubleParam0 = newValue;
        } else {
            objectParam0 = newValue;
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean getBoolean1() {
        return doubleParam1 != 0;
    }

    /** {@inheritDoc} */
    @Override
    public void setBoolean1(final boolean newValue) {
        doubleParam1 = newValue ? 1 : 0;
    }

    /** {@inheritDoc} */
    @Override
    public byte getByte1() {
        return (byte) doubleParam1;
    }

    /** {@inheritDoc} */
    @Override
    public void setByte1(final byte newValue) {
        doubleParam1 = newValue;
    }

    /** {@inheritDoc} */
    @Override
    public char getChar1() {
        return (char) doubleParam1;
    }

    /** {@inheritDoc} */
    @Override
    public void setChar1(final char newValue) {
        doubleParam1 = newValue;
    }

    /** {@inheritDoc} */
    @Override
    public short getShort1() {
        return (short) doubleParam1;
    }

    /** {@inheritDoc} */
    @Override
    public void setShort1(final short newValue) {
        doubleParam1 = newValue;
    }

    /** {@inheritDoc} */
    @Override
    public int getInt1() {
        return (int) doubleParam1;
    }

    /** {@inheritDoc} */
    @Override
    public void setInt1(final int newValue) {
        doubleParam1 = newValue;
    }

    /** {@inheritDoc} */
    @Override
    public float getFloat1() {
        return (float) doubleParam1;
    }

    /** {@inheritDoc} */
    @Override
    public void setFloat1(final float newValue) {
        doubleParam1 = newValue;
    }

    /** {@inheritDoc} */
    @Override
    public long getLong1() {
        if (objectParam1 instanceof Long) {
            return (Long) objectParam1;
        }
        return (long) doubleParam1;
    }

    /** {@inheritDoc} */
    @Override
    public void setLong1(final long newValue) {
        if ((newValue >= MIN_LONG_VALUE) && (newValue <= MAX_LONG_VALUE)) {
            doubleParam1 = newValue;
        } else {
            objectParam1 = newValue;
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean getBoolean2() {
        return doubleParam2 != 0;
    }

    /** {@inheritDoc} */
    @Override
    public void setBoolean2(final boolean newValue) {
        doubleParam2 = newValue ? 1 : 0;
    }

    /** {@inheritDoc} */
    @Override
    public byte getByte2() {
        return (byte) doubleParam2;
    }

    /** {@inheritDoc} */
    @Override
    public void setByte2(final byte newValue) {
        doubleParam2 = newValue;
    }

    /** {@inheritDoc} */
    @Override
    public char getChar2() {
        return (char) doubleParam2;
    }

    /** {@inheritDoc} */
    @Override
    public void setChar2(final char newValue) {
        doubleParam2 = newValue;
    }

    /** {@inheritDoc} */
    @Override
    public short getShort2() {
        return (short) doubleParam2;
    }

    /** {@inheritDoc} */
    @Override
    public void setShort2(final short newValue) {
        doubleParam2 = newValue;
    }

    /** {@inheritDoc} */
    @Override
    public int getInt2() {
        return (int) doubleParam2;
    }

    /** {@inheritDoc} */
    @Override
    public void setInt2(final int newValue) {
        doubleParam2 = newValue;
    }

    /** {@inheritDoc} */
    @Override
    public float getFloat2() {
        return (float) doubleParam2;
    }

    /** {@inheritDoc} */
    @Override
    public void setFloat2(final float newValue) {
        doubleParam2 = newValue;
    }

    /** {@inheritDoc} */
    @Override
    public long getLong2() {
        if (objectParam2 instanceof Long) {
            return (Long) objectParam2;
        }
        return (long) doubleParam2;
    }

    /** {@inheritDoc} */
    @Override
    public void setLong2(final long newValue) {
        if ((newValue >= MIN_LONG_VALUE) && (newValue <= MAX_LONG_VALUE)) {
            doubleParam2 = newValue;
        } else {
            objectParam2 = newValue;
        }
    }
}
