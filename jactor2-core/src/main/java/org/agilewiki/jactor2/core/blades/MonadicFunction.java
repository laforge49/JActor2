package org.agilewiki.jactor2.core.blades;

public interface MonadicFunction<Immutable> {
    BladeMonad<Immutable> f(BladeMonad<Immutable> bladeMonad);
}
