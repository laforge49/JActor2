package org.agilewiki.jactor2.core.blades.transmutable;

/**
 * A transmutable String.
 */
public class TransmutableString implements Transmutable<String> {
    protected volatile String unmodifiable;
    public final StringBuilder stringBuilder;

    public TransmutableString() {
        stringBuilder = new StringBuilder();
        createUnmodifiable();
    }

    public TransmutableString(String _string) {
        stringBuilder = new StringBuilder(_string);
        createUnmodifiable();
    }

    @Override
    public String getUnmodifiable() {
        return unmodifiable;
    }

    @Override
    public void createUnmodifiable() {
        unmodifiable = stringBuilder.toString();
    }

    @Override
    public Transmutable<String> recover() {
        return new TransmutableString(unmodifiable);
    }
}
