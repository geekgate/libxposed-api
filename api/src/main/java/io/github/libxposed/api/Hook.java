package io.github.libxposed.api;

import androidx.annotation.NonNull;

public interface Hook<T extends Pre.Context, V extends Post.Context> extends Pre<T>, Post<V> {

    @Override
    default void inject(@NonNull T ctx, @NonNull Object[] args) {}

    @Override
    default void inject(@NonNull V ctx, Object result, Throwable throwable) {}
}
