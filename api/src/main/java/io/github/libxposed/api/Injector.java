package io.github.libxposed.api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Injector interface, cannot use this interface directly, use {@link PreInjector}, {@link PostInjector} or {@link Hook} instead.
 * @author KeepItLight
 */
public interface Injector {
    @FunctionalInterface
    interface PreInjector extends Injector {
        void inject(@NonNull XposedInterface.BeforeHookCallback callback, @NonNull Object[] args);
    }
    @FunctionalInterface
    interface PostInjector extends Injector {
        void inject(@NonNull XposedInterface.AfterHookCallback callback, @NonNull Object result, @Nullable Throwable throwable);
    }
    interface Hook extends PreInjector, PostInjector {
        @Override
        default void inject(@NonNull XposedInterface.BeforeHookCallback callback, @NonNull Object[] args) {
        }
        @Override
        default void inject(@NonNull XposedInterface.AfterHookCallback callback, @NonNull Object result, @Nullable Throwable throwable) {
        }
    }
}
