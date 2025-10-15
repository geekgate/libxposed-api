package io.github.libxposed.api;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Interface for canceling a hook.
 *
 * @param <T> {@link Method} or {@link Constructor}
 */
@SuppressWarnings("unused")
public interface Handler<T> {
    /**
     * Gets the original method / constructor to be hooked.
     *
     * @return The original method / constructor, or {@code null} if the original method is static or not available.
     */
    T getOrigin();

    /**
     * Cancels the hook. The behavior of calling this method multiple times is undefined.
     */
    void cancel();
    /**
     * Enables the hook if it is a Stateful hook.
     */
    void enable();
    /**
     * Disables the hook if it is a Stateful hook.
     */
    void disable();
}