package org.agilewiki.pactor;

public interface Mailbox {
    public Mailbox createMailbox();
    public void addAutoClosable(AutoCloseable closeable);
    public void shutdown();
}
