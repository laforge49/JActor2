package org.agilewiki.pactor.ancestor;

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

    protected Ancestor parent;
    private boolean initialized;

    @Override
    public Ancestor getParent() {
        return parent;
    }

    public void initialize(final Ancestor _parent) {
        if (initialized)
            throw new IllegalStateException("Already initialized");
        initialized = true;
        parent = _parent;
    }
}
