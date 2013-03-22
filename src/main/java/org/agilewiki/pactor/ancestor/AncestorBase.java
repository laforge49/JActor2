package org.agilewiki.pactor.ancestor;

import org.agilewiki.pactor.Mailbox;
import org.agilewiki.pactor.ancestor.Ancestor;

public class AncestorBase implements Ancestor {
    public static Ancestor getAncestor(final Ancestor child, final Class targetClass) {
        if (child == null)
            return null;
        return getMatch(child.getParent(), targetClass);
    }

    public static Ancestor getMatch(Ancestor ancestor, final Class targetClass) {
        while (ancestor != null) {
            if (targetClass.isInstance(ancestor))
                return ancestor;
            ancestor = ancestor.getParent();
        }
        return null;
    }

    protected Mailbox mailbox;
    protected Ancestor parent;
    private boolean initialized;

    @Override
    public Ancestor getParent() {
        return parent;
    }

    public void initialize() {
        initialize(null, null);
    }

    public void initialize(final Mailbox _mailbox) {
        initialize(_mailbox, null);
    }

    public void initialize(final Ancestor _parent) {
        initialize(null, _parent);
    }

    public void initialize(final Mailbox _mailbox, final Ancestor _parent) {
        if (initialized)
            throw new IllegalStateException("Already initialized");
        initialized = true;
        mailbox = _mailbox;
        parent = _parent;
    }
}
