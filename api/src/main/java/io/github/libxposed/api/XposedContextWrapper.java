package io.github.libxposed.api;

import android.annotation.SuppressLint;
import android.content.ContextWrapper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;

import io.github.libxposed.api.utils.DexParser;

/**
 * Wrap of {@link XposedContext} used by the modules for the purpose of shielding framework implementation details.
 */
@SuppressWarnings({"unused"})
@SuppressLint("DiscouragedApi")
public class XposedContextWrapper extends ContextWrapper implements XposedInterface {

    XposedContextWrapper(@NonNull XposedContext base) {
        super(base);
    }

    XposedContextWrapper(@NonNull XposedContextWrapper base) {
        super(base);
    }

    /**
     * Get the Xposed API version of current implementation.
     *
     * @return API version
     */
    public final int getAPIVersion() {
        return API;
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public final XposedContext getBaseContext() {
        return (XposedContext) super.getBaseContext();
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public final String getFrameworkName() {
        return getBaseContext().getFrameworkName();
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public final String getFrameworkVersion() {
        return getBaseContext().getFrameworkVersion();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final long getFrameworkVersionCode() {
        return getBaseContext().getFrameworkVersionCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int getFrameworkPrivilege() {
        return getBaseContext().getFrameworkPrivilege();
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public final Object featuredMethod(String name, Object... args) {
        return getBaseContext().featuredMethod(name, args);
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public final Unhooker hook(@NonNull Method origin, @NonNull Injector injector) {
        return getBaseContext().hook(origin, injector);
    }
    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public final Unhooker hook(@NonNull Method origin, int priority, @NonNull Injector injector) {
        return getBaseContext().hook(origin, priority, injector);
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public final Unhooker hook(@NonNull Constructor<?> origin, @NonNull Injector injector) {
        return getBaseContext().hook(origin, injector);
    }
    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public final Unhooker hook(@NonNull Constructor<?> origin, int priority, @NonNull Injector injector) {
        return getBaseContext().hook(origin, priority, injector);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean deoptimize(@NonNull Method method) {
        return getBaseContext().deoptimize(method);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final <T> boolean deoptimize(@NonNull Constructor<T> constructor) {
        return getBaseContext().deoptimize(constructor);
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public final Object invokeOrigin(@NonNull Method method, @Nullable Object thisObject, Object... args) throws InvocationTargetException, IllegalArgumentException, IllegalAccessException {
        return getBaseContext().invokeOrigin(method, thisObject, args);
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public final Object invokeSpecial(@NonNull Method method, @NonNull Object thisObject, Object... args) throws InvocationTargetException, IllegalArgumentException, IllegalAccessException {
        return getBaseContext().invokeSpecial(method, thisObject, args);
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public final <T> T newInstanceOrigin(@NonNull Constructor<T> constructor, Object... args) throws InvocationTargetException, IllegalArgumentException, IllegalAccessException, InstantiationException {
        return getBaseContext().newInstanceOrigin(constructor, args);
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public final <T, U> U newInstanceSpecial(@NonNull Constructor<T> constructor, @NonNull Class<U> subClass, Object... args) throws InvocationTargetException, IllegalArgumentException, IllegalAccessException, InstantiationException {
        return getBaseContext().newInstanceSpecial(constructor, subClass, args);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void log(@NonNull String message) {
        getBaseContext().log(message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void log(@NonNull String message, @NonNull Throwable throwable) {
        getBaseContext().log(message, throwable);
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public final DexParser parseDex(@NonNull ByteBuffer dexData, boolean includeAnnotations) throws IOException {
        return getBaseContext().parseDex(dexData, includeAnnotations);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void attachBaseContext(android.content.Context base) {
        if (base instanceof XposedContext || base instanceof XposedContextWrapper) {
            super.attachBaseContext(base);
        } else {
            throw new IllegalArgumentException();
        }
    }
}
