package io.github.libxposed.api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Executable;

/**
 * Post-injector runs after the original method / constructor is invoked.
 */
@FunctionalInterface @SuppressWarnings("unused")
public interface Post extends Injector {

    /**
     * Callbacks after the original method / constructor is invoked.
     *
     * @param ctx The context
     * @param result The return value of the method or the before invocation callback. If the procedure is a
     *               constructor, a void method or an exception was thrown, the return value will be {@code null}.
     * @param throwable The exception thrown by the method / constructor or the before invocation callback. If the
     *                  procedure call was successful, the return value will be {@code null}.
     */
    void inject(@NonNull Context ctx, Object result, Throwable throwable);

    /**
     * Contextual interface for after invocation callbacks.
     */
    interface Context extends Injector.Context {

        /**
         * Gets the arguments passed to the method / constructor. You can modify the arguments.
         */
        @NonNull
        Object[] getArgs();

        /**
         * Gets whether the invocation was skipped by the before invocation callback.
         */
        boolean isSkipped();

        /**
         * Sets the return value of the method and skip the invocation. If the procedure is a constructor,
         * the {@code result} param will be ignored.
         *
         * @param result The return value
         */
        void setResult(@Nullable Object result);

        /**
         * Sets the exception thrown by the method / constructor.
         *
         * @param throwable The exception to be thrown.
         */
        void setThrowable(@Nullable Throwable throwable);
    }

    class ContextWrapper implements Context {

        private final Context src;

        public ContextWrapper(Context src) {
            this.src = src;
        }

        @NonNull
        @Override
        public Object[] getArgs() {
            return src.getArgs();
        }

        @Override
        public boolean isSkipped() {
            return src.isSkipped();
        }

        @Override
        public void setResult(@Nullable Object result) {
            src.setResult( result );
        }

        @Override
        public void setThrowable(@Nullable Throwable throwable) {
            src.setThrowable( throwable );
        }

        @NonNull
        @Override
        public Executable getTarget() {
            return src.getTarget();
        }

        @Nullable
        @Override
        public Object getThisObject() {
            return src.getThisObject();
        }

        @Nullable @Override
        public Object getThat() {
            return src.getThisObject();
        }
    }
}
