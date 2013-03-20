package org.agilewiki.pactor;

public final class PActor {
    private PActor() {
        throw new UnsupportedOperationException();
    }

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
}
