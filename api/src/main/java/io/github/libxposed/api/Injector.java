package io.github.libxposed.api;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * Injector interface, cannot use this interface directly, use {@link Pre}, {@link Post} or {@link Hook} instead.
 * @author KeepItLight
 */
@SuppressWarnings("unused")
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

    default void done(Context ctx) {}

    default int getPriority() {
        return Injector.PRIORITY_DEFAULT;
    }

    /**
     * Interface for canceling a hook.
     *
     * @param <T> {@link Method} or {@link Constructor}
     */
    @SuppressWarnings("unused")
    interface Handler<T extends Executable> extends io.github.libxposed.api.Handler, Stateful.Default {
        /**
         * Gets the original method / constructor to be hooked.
         *
         * @return The original method / constructor, or {@code null} if the original method is static or not available.
         */
        T getOrigin();
    }

    @FunctionalInterface
    interface Task extends Callable<io.github.libxposed.api.Handler> {
    }

    /**
     * Contextual interface for before invocation callbacks.
     */
    interface Context {
        String TAG = "Injected";

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
        /**
         * Gets the {@code this} object, or {@code null} if the method is static.
         */
        @Nullable
        Object getThat();

        @RequiresApi(api = Build.VERSION_CODES.O)
        default void trace(String tag, String entry) {
            if (entry == null || entry.isEmpty()) {
                var t = getTarget();
                entry = t.getName() + " <- " + t.getDeclaringClass().getCanonicalName();
            }
            Log.i(tag != null && !tag.isEmpty() ? tag : TAG, entry);
        }
        @RequiresApi(api = Build.VERSION_CODES.O)
        default void trace(String entry) {
            trace(TAG, entry);
        }
        @RequiresApi(api = Build.VERSION_CODES.O)
        default void trace() {
            trace(TAG, null);
        }

        default void log(@NonNull Object... args) {
            var b = new StringBuilder();
            for (Object arg : args) {
                b.append(arg);
            }
            Log.i(TAG, b.toString());
        }
        default void log(String msg, Throwable throwable) {
            Log.e(TAG, msg, throwable);
        }
        default void log(Throwable throwable) {
            Log.e(TAG, throwable.getMessage(), throwable);
        }
        default void log(String err) {
            Log.e(TAG, err);
        }
    }
}
