package io.github.libxposed.api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Executable;

/**
 * Injector interface, cannot use this interface directly, use {@link Pre}, {@link Post} or {@link Hook} instead.
 * @author KeepItLight
 */
public interface Injector {

    /**
     * The default hook priority.
     */
    int PRIORITY_DEFAULT = 50;
    /**
     * Execute the hook callback late.
     */
    int PRIORITY_LOWEST = -10000;
    /**
     * Execute the hook callback early.
     */
    int PRIORITY_HIGHEST = 10000;

    interface Lifecycle {
        /**
         * Called when the hook is ready.
         */
        void ready();
        /**
         * Called when the hook is pre-injected.
         */
        void enter();
        /**
         * Called when the hook is completed.
         */
        void done();
    }

    /**
     * Contextual interface for before invocation callbacks.
     */
    interface Context {
        /**
         * Gets the method / constructor to be hooked.
         */
        @NonNull
        Executable getTarget();

        /**
         * Gets the {@code this} object, or {@code null} if the method is static.
         */
        @Nullable
        Object getThisObject();
    }
}
