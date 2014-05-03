package org.agilewiki.jactor2.core.blades;

public interface MonadicFunction<Immutable> {
    Immutable f(BladeMonad<Immutable> source, BladeMonad<Immutable> target);
}
