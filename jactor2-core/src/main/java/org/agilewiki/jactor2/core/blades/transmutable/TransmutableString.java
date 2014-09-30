package org.agilewiki.jactor2.core.blades.transmutable;

/**
 * A transmutable String.
 */
public class TransmutableString implements Transmutable<String> {
    public final StringBuilder stringBuilder;

    public TransmutableString() {
        stringBuilder = new StringBuilder();
    }

    public TransmutableString(String _string) {
        stringBuilder = new StringBuilder(_string);
    }

    @Override
    public String createUnmodifiable() {
        return stringBuilder.toString();
    }

    @Override
    public TransmutableString recreate(String unmodifiable) {
        return new TransmutableString(unmodifiable);
    }
}
