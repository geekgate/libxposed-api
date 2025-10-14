package io.github.libxposed.api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Executable;

/**
 * Pre-injector runs before the original method / constructor is invoked.
 */
@FunctionalInterface @SuppressWarnings("unused")
public interface Pre<T extends Pre.Context> extends Injector {

    /**
     * Wraps the original context.
     *
     * @param src The original context
     * @return The wrapped context
     */
    default T wrap(Context src) {
        return null;
    }

    /**
     * Callbacks before the original method / constructor is invoked.
     *
     * @param ctx The context
     * @param args The arguments passed to the method / constructor
     */
    void inject(@NonNull T ctx, @NonNull Object[] args);

    /**
     * Contextual interface for before invocation callbacks.
     */
    interface Context extends Injector.Context {
        /**
         * Sets the return value of the method and skip the invocation. If the procedure is a constructor,
         * the {@code result} param will be ignored.
         * Note that the after invocation callback will still be called.
         *
         * @param result The return value
         */
        void returnAndSkip(@Nullable Object result);

        /**
         * Throw an exception from the method / constructor and skip the invocation.
         * Note that the after invocation callback will still be called.
         *
         * @param throwable The exception to be thrown
         */
        void throwAndSkip(@Nullable Throwable throwable);
    }

    abstract class ContextBase implements Context {

        private final Context src;

        public ContextBase(Context src) {
            this.src = src;
        }

        @Override
        public void returnAndSkip(@Nullable Object result) {
            src.returnAndSkip(result);
        }

        @Override
        public void throwAndSkip(@Nullable Throwable throwable) {
            src.returnAndSkip(throwable);
        }

        @NonNull @Override
        public Executable getTarget() {
            return src.getTarget();
        }

        @Nullable @Override
        public Object getThisObject() {
            return src.getThisObject();
        }
    }

    interface Default extends Pre<Context> {
    }
}
