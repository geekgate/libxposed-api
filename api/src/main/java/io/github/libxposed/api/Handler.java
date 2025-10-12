package io.github.libxposed.api;


import androidx.annotation.NonNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Interface for canceling a hook.
 *
 * @param <T> {@link Method} or {@link Constructor}
 */
public interface Handler<T> {
    /**
     * Gets the method or constructor being hooked.
     */
    @NonNull
    T getOrigin();

    /**
     * Cancels the hook. The behavior of calling this method multiple times is undefined.
     */
    void cancel();
}