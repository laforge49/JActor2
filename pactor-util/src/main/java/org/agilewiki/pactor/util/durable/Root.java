package org.agilewiki.pactor.util.durable;

public interface Root extends Box {

    public static final String FACTORY_NAME = "root";

    int save(final byte[] _bytes, final int _offset);

    int load(final byte[] _bytes, final int _offset, final int _length);

    void load(final byte[] _bytes);

    String getDescriptor();
}
