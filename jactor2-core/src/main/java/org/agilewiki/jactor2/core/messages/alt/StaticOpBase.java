package org.agilewiki.jactor2.core.messages.alt;

import org.agilewiki.jactor2.core.blades.Blade;
import org.agilewiki.jactor2.core.messages.Operation;
import org.agilewiki.jactor2.core.messages.impl.RequestImplWithData;
import org.agilewiki.jactor2.core.reactors.Reactor;

import java.util.Objects;

/**
 * A static abstract operation.
 */
public abstract class StaticOpBase<B extends Blade, RESPONSE_TYPE, RIWD extends RequestImplWithData<RESPONSE_TYPE>>
        implements Operation<RESPONSE_TYPE> {
    /**
     * Empty Var array.
     */
    private static final Var[] NO_VARS = new Var[0];

    private static volatile int nextHash;

    /**
     * Number of double variables defined until now.
     */
    private int doubles = 0;

    /**
     * Number of object variables defined until now.
     */
    private int objects = 0;

    /**
     * All the variables.
     */
    private Var[] vars = NO_VARS;

    /**
     * The Blade variable.
     */
    protected final ObjectVar<B> blade;

    /**
     * Our hashcode.
     */
    private final int hashCode = nextHash++;

    /**
     * Base class for variables.
     */
    private static abstract class Var {
        /**
         * Initialize the value of this variable in a request.
         */
        public abstract void init(RequestImplWithData<?> _requestImpl);
    }

    /**
     * Base class for primitive variables.
     */
    private abstract class PrimitiveVar extends Var {
        protected final int index;

        protected PrimitiveVar() {
            index = doubles++;
            if (index > 2) {
                throw new IllegalStateException("Too many PrimitiveVars!");
            }
            addVar(this);
        }
    }

    /**
     * Base class for Object variables.
     */
    private abstract class NonPrimitiveVar extends Var {
        protected final int index;

        protected NonPrimitiveVar() {
            index = objects++;
            if (index > 2) {
                throw new IllegalStateException("Too many ObjectVars!");
            }
            addVar(this);
        }
    }

    /**
     * "boolean" primitive variable.
     */
    protected final class BooleanVar extends PrimitiveVar {
        /**
         * The default value.
         */
        private final boolean defVal;

        /**
         * Creates a variable with _defVal as default value.
         */
        private BooleanVar(final boolean _defVal) {
            defVal = _defVal;
        }

        /**
         * Initialize the value of this variable in a request.
         */
        @Override
        public void init(final RequestImplWithData<?> _requestImpl) {
            set(_requestImpl, defVal);
        }

        /**
         * Returns the value.
         */
        public boolean get(final RequestImplWithData<?> _requestImpl) {
            return _requestImpl.getDouble(index) != 0;
        }

        /**
         * Sets the value.
         */
        public <R extends RequestImplWithData<?>> R set(final R _requestImpl,
                                                        final boolean newValue) {
            _requestImpl.setDouble(index, newValue ? 1 : 0);
            return _requestImpl;
        }
    }

    /**
     * "byte" primitive variable.
     */
    protected final class ByteVar extends PrimitiveVar {
        /**
         * The default value.
         */
        private final byte defVal;

        /**
         * Creates a variable with _defVal as default value.
         */
        private ByteVar(final byte _defVal) {
            defVal = _defVal;
        }

        /**
         * Initialize the value of this variable in a request.
         */
        @Override
        public void init(final RequestImplWithData<?> _requestImpl) {
            set(_requestImpl, defVal);
        }

        /**
         * Returns the value.
         */
        public byte get(final RequestImplWithData<?> _requestImpl) {
            return (byte) _requestImpl.getDouble(index);
        }

        /**
         * Sets the value.
         */
        public <R extends RequestImplWithData<?>> R set(final R _requestImpl,
                                                        final byte newValue) {
            _requestImpl.setDouble(index, newValue);
            return _requestImpl;
        }

        /**
         * Increments the value, and returns the pre-increment value (works like v++).
         */
        public byte inc(final RequestImplWithData<?> _requestImpl) {
            final byte result = get(_requestImpl);
            set(_requestImpl, (byte) (result + 1));
            return result;
        }

        /**
         * Decrements the value, and returns the pre-increment value (works like v--).
         */
        public byte dec(final RequestImplWithData<?> _requestImpl) {
            final byte result = get(_requestImpl);
            set(_requestImpl, (byte) (result - 1));
            return result;
        }
    }

    /**
     * "char" primitive variable.
     */
    protected final class CharVar extends PrimitiveVar {
        /**
         * The default value.
         */
        private final char defVal;

        /**
         * Creates a variable with _defVal as default value.
         */
        private CharVar(final char _defVal) {
            defVal = _defVal;
        }

        /**
         * Initialize the value of this variable in a request.
         */
        @Override
        public void init(final RequestImplWithData<?> _requestImpl) {
            set(_requestImpl, defVal);
        }

        /**
         * Returns the value.
         */
        public char get(final RequestImplWithData<?> _requestImpl) {
            return (char) _requestImpl.getDouble(index);
        }

        /**
         * Sets the value.
         */
        public <R extends RequestImplWithData<?>> R set(final R _requestImpl,
                                                        final char newValue) {
            _requestImpl.setDouble(index, newValue);
            return _requestImpl;
        }

        /**
         * Increments the value, and returns the pre-increment value (works like v++).
         */
        public char inc(final RequestImplWithData<?> _requestImpl) {
            final char result = get(_requestImpl);
            set(_requestImpl, (char) (result + 1));
            return result;
        }

        /**
         * Decrements the value, and returns the pre-increment value (works like v--).
         */
        public char dec(final RequestImplWithData<?> _requestImpl) {
            final char result = get(_requestImpl);
            set(_requestImpl, (char) (result - 1));
            return result;
        }
    }

    /**
     * "short" primitive variable.
     */
    protected final class ShortVar extends PrimitiveVar {
        /**
         * The default value.
         */
        private final short defVal;

        /**
         * Creates a variable with _defVal as default value.
         */
        private ShortVar(final short _defVal) {
            defVal = _defVal;
        }

        /**
         * Initialize the value of this variable in a request.
         */
        @Override
        public void init(final RequestImplWithData<?> _requestImpl) {
            set(_requestImpl, defVal);
        }

        /**
         * Returns the value.
         */
        public short get(final RequestImplWithData<?> _requestImpl) {
            return (short) _requestImpl.getDouble(index);
        }

        /**
         * Sets the value.
         */
        public <R extends RequestImplWithData<?>> R set(final R _requestImpl,
                                                        final short newValue) {
            _requestImpl.setDouble(index, newValue);
            return _requestImpl;
        }

        /**
         * Increments the value, and returns the pre-increment value (works like v++).
         */
        public short inc(final RequestImplWithData<?> _requestImpl) {
            final short result = get(_requestImpl);
            set(_requestImpl, (short) (result + 1));
            return result;
        }

        /**
         * Decrements the value, and returns the pre-increment value (works like v--).
         */
        public short dec(final RequestImplWithData<?> _requestImpl) {
            final short result = get(_requestImpl);
            set(_requestImpl, (short) (result - 1));
            return result;
        }
    }

    /**
     * "int" primitive variable.
     */
    protected final class IntVar extends PrimitiveVar {
        /**
         * The default value.
         */
        private final int defVal;

        /**
         * Creates a variable with _defVal as default value.
         */
        private IntVar(final int _defVal) {
            defVal = _defVal;
        }

        /**
         * Initialize the value of this variable in a request.
         */
        @Override
        public void init(final RequestImplWithData<?> _requestImpl) {
            set(_requestImpl, defVal);
        }

        /**
         * Returns the value.
         */
        public int get(final RequestImplWithData<?> _requestImpl) {
            return (int) _requestImpl.getDouble(index);
        }

        /**
         * Sets the value.
         */
        public <R extends RequestImplWithData<?>> R set(final R _requestImpl,
                                                        final int newValue) {
            _requestImpl.setDouble(index, newValue);
            return _requestImpl;
        }

        /**
         * Increments the value, and returns the pre-increment value (works like v++).
         */
        public int inc(final RequestImplWithData<?> _requestImpl) {
            final int result = get(_requestImpl);
            set(_requestImpl, (result + 1));
            return result;
        }

        /**
         * Decrements the value, and returns the pre-increment value (works like v--).
         */
        public int dec(final RequestImplWithData<?> _requestImpl) {
            final int result = get(_requestImpl);
            set(_requestImpl, (result - 1));
            return result;
        }
    }

    /**
     * "float" primitive variable.
     */
    protected final class FloatVar extends PrimitiveVar {
        /**
         * The default value.
         */
        private final float defVal;

        /**
         * Creates a variable with _defVal as default value.
         */
        private FloatVar(final float _defVal) {
            defVal = _defVal;
        }

        /**
         * Initialize the value of this variable in a request.
         */
        @Override
        public void init(final RequestImplWithData<?> _requestImpl) {
            set(_requestImpl, defVal);
        }

        /**
         * Returns the value.
         */
        public float get(final RequestImplWithData<?> _requestImpl) {
            return (float) _requestImpl.getDouble(index);
        }

        /**
         * Sets the value.
         */
        public <R extends RequestImplWithData<?>> R set(final R _requestImpl,
                                                        final float newValue) {
            _requestImpl.setDouble(index, newValue);
            return _requestImpl;
        }

        /**
         * Increments the value, and returns the pre-increment value (works like v++).
         */
        public float inc(final RequestImplWithData<?> _requestImpl) {
            final float result = get(_requestImpl);
            set(_requestImpl, (result + 1));
            return result;
        }

        /**
         * Decrements the value, and returns the pre-increment value (works like v--).
         */
        public float dec(final RequestImplWithData<?> _requestImpl) {
            final float result = get(_requestImpl);
            set(_requestImpl, (result - 1));
            return result;
        }
    }

    /**
     * "double" primitive variable.
     */
    protected final class DoubleVar extends PrimitiveVar {
        /**
         * The default value.
         */
        private final double defVal;

        /**
         * Creates a variable with _defVal as default value.
         */
        private DoubleVar(final double _defVal) {
            defVal = _defVal;
        }

        /**
         * Initialize the value of this variable in a request.
         */
        @Override
        public void init(final RequestImplWithData<?> _requestImpl) {
            set(_requestImpl, defVal);
        }

        /**
         * Returns the value.
         */
        public double get(final RequestImplWithData<?> _requestImpl) {
            return _requestImpl.getDouble(index);
        }

        /**
         * Sets the value.
         */
        public <R extends RequestImplWithData<?>> R set(final R _requestImpl,
                                                        final double newValue) {
            _requestImpl.setDouble(index, newValue);
            return _requestImpl;
        }

        /**
         * Increments the value, and returns the pre-increment value (works like v++).
         */
        public double inc(final RequestImplWithData<?> _requestImpl) {
            final double result = get(_requestImpl);
            set(_requestImpl, (result + 1));
            return result;
        }

        /**
         * Decrements the value, and returns the pre-increment value (works like v--).
         */
        public double dec(final RequestImplWithData<?> _requestImpl) {
            final double result = get(_requestImpl);
            set(_requestImpl, (result - 1));
            return result;
        }
    }

    /**
     * "long" primitive variable, stored as an Object.
     */
    protected final class LongVar extends NonPrimitiveVar {
        /**
         * The default value.
         */
        private final Long defVal;

        /**
         * Creates a variable with _defVal as default value.
         */
        private LongVar(final long _defVal) {
            defVal = _defVal;
        }

        /**
         * Initialize the value of this variable in a request.
         */
        @Override
        public void init(final RequestImplWithData<?> _requestImpl) {
            _requestImpl.setObject(index, defVal);
        }

        /**
         * Returns the value.
         */
        public long get(final RequestImplWithData<?> _requestImpl) {
            return (Long) _requestImpl.getObject(index);
        }

        /**
         * Sets the value.
         */
        public <R extends RequestImplWithData<?>> R set(final R _requestImpl,
                                                        final long newValue) {
            _requestImpl.setObject(index, newValue);
            return _requestImpl;
        }

        /**
         * Increments the value, and returns the pre-increment value (works like v++).
         */
        public long inc(final RequestImplWithData<?> _requestImpl) {
            final long result = get(_requestImpl);
            set(_requestImpl, (result + 1));
            return result;
        }

        /**
         * Decrements the value, and returns the pre-increment value (works like v--).
         */
        public long dec(final RequestImplWithData<?> _requestImpl) {
            final long result = get(_requestImpl);
            set(_requestImpl, (result - 1));
            return result;
        }
    }

    /**
     * Object variable.
     */
    protected final class ObjectVar<E> extends NonPrimitiveVar {
        /**
         * The default value.
         */
        private final E defVal;

        /**
         * Initialize the value of this variable in a request.
         */
        @Override
        public void init(final RequestImplWithData<?> _requestImpl) {
            _requestImpl.setObject(index, defVal);
        }

        /**
         * The type.
         */
        private final Class<E> type;

        /**
         * Creates an Object variable, with type and default value.
         */
        private ObjectVar(final Class<E> _type, final E _defVal) {
            type = Objects.requireNonNull(_type, "type");
            if (_defVal != null) {
                if (_defVal.getClass() != type) {
                    // TODO This does not work in GWT
//                if (!type.isInstance(_defVal)) {
                    throw new IllegalArgumentException("A "
                            + _defVal.getClass() + " is not a " + type);
                }
            }
            defVal = _defVal;
        }

        /**
         * Returns the value.
         */
        @SuppressWarnings("unchecked")
        public E get(final RequestImplWithData<?> _requestImpl) {
            return (E) _requestImpl.getObject(index);
            // TODO This does not work in GWT
//            return type.cast(_requestImpl.getObject(index));
        }

        /**
         * Sets the value.
         */
        public <R extends RequestImplWithData<?>> R set(final R _requestImpl,
                                                        final E newValue) {
            _requestImpl.setObject(index, newValue);
            return _requestImpl;
        }
    }

    /**
     * Adds a new variable.
     */
    private void addVar(final Var var) {
        final Var[] newArray = new Var[vars.length + 1];
        for (int i = 0; i < vars.length; i++) {
            newArray[i] = vars[i];
        }
        newArray[vars.length] = var;
        vars = newArray;
    }

    public final String opName;

    /**
     * If this is an inner class, remove the outer name.
     */
    private static String removeOuter(final Class<?> clazz) {
        final String result = clazz.getSimpleName();
        final int sep = result.lastIndexOf('$');
        return (sep < 0) ? result : result.substring(sep + 1);
    }

    /**
     * Creates and returns a new BooleanVar.
     */
    public final BooleanVar var(final boolean defVal) {
        return new BooleanVar(defVal);
    }

    /**
     * Creates and returns a new ByteVar.
     */
    public final ByteVar var(final byte defVal) {
        return new ByteVar(defVal);
    }

    /**
     * Creates and returns a new CharVar.
     */
    public final CharVar var(final char defVal) {
        return new CharVar(defVal);
    }

    /**
     * Creates and returns a new ShortVar.
     */
    public final ShortVar var(final short defVal) {
        return new ShortVar(defVal);
    }

    /**
     * Creates and returns a new IntVar.
     */
    public final IntVar var(final int defVal) {
        return new IntVar(defVal);
    }

    /**
     * Creates and returns a new FloatVar.
     */
    public final FloatVar var(final float defVal) {
        return new FloatVar(defVal);
    }

    /**
     * Creates and returns a new DoubleVar.
     */
    public final DoubleVar var(final double defVal) {
        return new DoubleVar(defVal);
    }

    /**
     * Creates and returns a new LongVar.
     */
    public final LongVar var(final long defVal) {
        return new LongVar(defVal);
    }

    /**
     * Creates and returns a new ObjectVar&lt;E&gt;.
     */
    public final <E> ObjectVar<E> var(final Class<E> type) {
        return new ObjectVar<E>(type, null);
    }

    /**
     * Creates and returns a new ObjectVar&lt;E&gt;.
     */
    public final <E> ObjectVar<E> var(final Class<E> type, final E defVal) {
        return new ObjectVar<E>(type, defVal);
    }

    /**
     * Create a static asynchronous operation.
     *
     * @param bladeType The class of the operation.
     */
    protected StaticOpBase(final Class<B> bladeType) {
        opName = removeOuter(getClass());
        blade = var(bladeType);
    }

    /**
     * Redefines the hashcode for a faster hashing.
     */
    @Override
    public int hashCode() {
        return hashCode;
    }

    /**
     * Creates a R Request.
     *
     * @param targetBlade The targetBlade, which is set to be the first parameter.
     * @return the R Request.
     */
    public final RIWD create(final B targetBlade) {
        final RIWD result = createInternalWithData(targetBlade.getReactor());
        for (final Var v : vars) {
            v.init(result);
        }
        blade.set(result, targetBlade);
        return result;
    }

    @Override
    public final String toString() {
        return opName;
    }

    /**
     * Creates a RequestImplWithData Request.
     *
     * @param targetReactor The target Reactor.
     * @return the RequestImplWithData Request.
     */
    protected abstract RIWD createInternalWithData(final Reactor targetReactor);
}
