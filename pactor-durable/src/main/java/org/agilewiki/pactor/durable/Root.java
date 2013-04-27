package org.agilewiki.pactor.durable;

public interface Root extends Box {

    public static final String FACTORY_NAME = "root";

    int save(final byte[] _bytes, final int _offset) throws Exception;

    int load(final byte[] _bytes, final int _offset, final int _length) throws Exception;

    void load(final byte[] _bytes)
            throws Exception;

    String getDescriptor();
}
