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
package org.agilewiki.jactor2.core.requests.impl;


/**
 * API for internal payload-carrying request implementations.
 *
 * The goal of this interface is to define a RequestImpl that can be used
 * directly, without requiring one additional object creation *per request*.
 * The "logic" of the request is already defined in an Operation, and in many
 * cases, this logic can be implemented as a singleton. But if this Operation
 * needs "variable" parameter to operate it's work, a new instance of the
 * Operation must be created with every request. By having a pre-defined native
 * request that can carry a small amount of "user payload" with itself, we
 * should be able to make most Operations into singletons.
 *
 * Most methods calls don't have more then 3 parameters, so we support 3
 * parameters, but since they could be either Objects or primitive values, we
 * support both double and Object as parameters, such that we have in effect 6
 * parameters.
 *
 * Remember that *all* primitive values can be *safely* stored in a double,
 * *except long*. Therefore, the Object parameter might be used, if the long
 * does not fit in a double.
 *
 * @param <RESPONSE_TYPE>    The return value type.
 *
 * @author monster
 */
public interface RequestImplWithData<RESPONSE_TYPE> extends
        RequestImpl<RESPONSE_TYPE> {
    /** Maximum integer value in a double. */
    long MAX_LONG_VALUE = (1L << 52L);

    /** Minimum integer value in a double. */
    long MIN_LONG_VALUE = -MAX_LONG_VALUE;

    /** Return the double parameter 0, as a boolean. */
    boolean getBoolean0();

    /** Sets the double parameter 0, as a boolean. */
    void setBoolean0(boolean newValue);

    /** Return the double parameter 0, as a byte. */
    byte getByte0();

    /** Sets the double parameter 0, as a byte. */
    void setByte0(byte newValue);

    /** Return the double parameter 0, as a char. */
    char getChar0();

    /** Sets the double parameter 0, as a char. */
    void setChar0(char newValue);

    /** Return the double parameter 0, as a short. */
    short getShort0();

    /** Sets the double parameter 0, as a short. */
    void setShort0(short newValue);

    /** Return the double parameter 0, as a int. */
    int getInt0();

    /** Sets the double parameter 0, as a int. */
    void setInt0(int newValue);

    /** Return the double parameter 0, as a float. */
    float getFloat0();

    /** Sets the double parameter 0, as a float. */
    void setFloat0(float newValue);

    /**
     * Return the parameter 0 as a long.
     * First checks if it was saved as Object, and if not, casts the double.
     */
    long getLong0();

    /**
     * Sets the parameter 0 as a long.
     * If small enough, sets it as a double, otherwise as an Object.
     */
    void setLong0(long newValue);

    /** Return the double parameter 0. */
    double getDouble0();

    /** Sets the double parameter 0. */
    void setDouble0(double newValue);

    /**
     * Return the Object parameter 0.
     * There is no validation on the casting.
     */
    <E> E getObject0();

    /** Sets the Object parameter 0. */
    void setObject0(Object newValue);

    /** Return the double parameter 1, as a boolean. */
    boolean getBoolean1();

    /** Sets the double parameter 1, as a boolean. */
    void setBoolean1(boolean newValue);

    /** Return the double parameter 1, as a byte. */
    byte getByte1();

    /** Sets the double parameter 1, as a byte. */
    void setByte1(byte newValue);

    /** Return the double parameter 1, as a char. */
    char getChar1();

    /** Sets the double parameter 1, as a char. */
    void setChar1(char newValue);

    /** Return the double parameter 1, as a short. */
    short getShort1();

    /** Sets the double parameter 1, as a short. */
    void setShort1(short newValue);

    /** Return the double parameter 1, as a int. */
    int getInt1();

    /** Sets the double parameter 1, as a int. */
    void setInt1(int newValue);

    /** Return the double parameter 1, as a float. */
    float getFloat1();

    /** Sets the double parameter 1, as a float. */
    void setFloat1(float newValue);

    /** Return the double parameter 1. */
    double getDouble1();

    /** Sets the double parameter 1. */
    void setDouble1(double newValue);

    /**
     * Return the parameter 1 as a long.
     * First checks if it was saved as Object, and if not, casts the double.
     */
    long getLong1();

    /**
     * Sets the parameter 1 as a long.
     * If small enough, sets it as a double, otherwise as an Object.
     */
    void setLong1(long newValue);

    /**
     * Return the Object parameter 1.
     * There is no validation on the casting.
     */
    <E> E getObject1();

    /** Sets the Object parameter 1. */
    void setObject1(Object newValue);

    /** Return the double parameter 2, as a boolean. */
    boolean getBoolean2();

    /** Sets the double parameter 2, as a boolean. */
    void setBoolean2(boolean newValue);

    /** Return the double parameter 2, as a byte. */
    byte getByte2();

    /** Sets the double parameter 2, as a byte. */
    void setByte2(byte newValue);

    /** Return the double parameter 2, as a char. */
    char getChar2();

    /** Sets the double parameter 2, as a char. */
    void setChar2(char newValue);

    /** Return the double parameter 2, as a short. */
    short getShort2();

    /** Sets the double parameter 2, as a short. */
    void setShort2(short newValue);

    /** Return the double parameter 2, as a int. */
    int getInt2();

    /** Sets the double parameter 2, as a int. */
    void setInt2(int newValue);

    /** Return the double parameter 2, as a float. */
    float getFloat2();

    /** Sets the double parameter 2, as a float. */
    void setFloat2(float newValue);

    /** Return the double parameter 2. */
    double getDouble2();

    /** Sets the double parameter 2. */
    void setDouble2(double newValue);

    /**
     * Return the parameter 2 as a long.
     * First checks if it was saved as Object, and if not, casts the double.
     */
    long getLong2();

    /**
     * Sets the parameter 2 as a long.
     * If small enough, sets it as a double, otherwise as an Object.
     */
    void setLong2(long newValue);

    /**
     * Return the Object parameter 2.
     * There is no validation on the casting.
     */
    <E> E getObject2();

    /** Sets the Object parameter 2. */
    void setObject2(Object newValue);
}
