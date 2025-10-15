package io.github.libxposed.api;


/**
 * Interface for canceling a set of hooks. When it is applied to a specific individual hook,
 * use {@link Injector.Handler} instead.
 */
@SuppressWarnings("unused")
public interface Handler extends Stateful {
    /**
     * Cancels the hook.
     */
    void cancel();
}