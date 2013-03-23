package org.agilewiki.pactor.ancestor;

import org.agilewiki.pactor.ActorBase;
import org.agilewiki.pactor.Mailbox;

public class AncestorBase extends ActorBase implements Ancestor {
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

    protected Ancestor parent;

    public void initialize(final Ancestor _parent) {
        initialize(null, _parent);
    }

    public void initialize(final Mailbox _mailbox, final Ancestor _parent) {
        super.initialize(_mailbox);
        parent = _parent;
    }

    @Override
    public Ancestor getParent() {
        return parent;
    }
}
