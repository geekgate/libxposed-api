package io.github.libxposed.api;

import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.os.ParcelFileDescriptor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;

import io.github.libxposed.api.errors.HookFailedError;
import io.github.libxposed.api.Injector.Handler;
import io.github.libxposed.api.utils.DexParser;

/**
 * Xposed interface for modules to operate on application processes.
 */
@SuppressWarnings("unused")
public interface XposedInterface {
    /**
     * SDK API version.
     */
    int API = 200;

    /**
     * Indicates that the framework is running as root.
     */
    int FRAMEWORK_PRIVILEGE_ROOT = 0;
    /**
     * Indicates that the framework is running in a container with a fake system_server.
     */
    int FRAMEWORK_PRIVILEGE_CONTAINER = 1;
    /**
     * Indicates that the framework is running as a different app, which may have at most shell permission.
     */
    int FRAMEWORK_PRIVILEGE_APP = 2;
    /**
     * Indicates that the framework is embedded in the hooked app,
     * which means {@link #getRemotePreferences} will be null and remote file is unsupported.
     */
    int FRAMEWORK_PRIVILEGE_EMBEDDED = 3;


    /**
     * Gets the Xposed framework name of current implementation.
     *
     * @return Framework name
     */
    @NonNull
    String getFrameworkName();

    /**
     * Gets the Xposed framework version of current implementation.
     *
     * @return Framework version
     */
    @NonNull
    String getFrameworkVersion();

    /**
     * Gets the Xposed framework version code of current implementation.
     *
     * @return Framework version code
     */
    long getFrameworkVersionCode();

    /**
     * Gets the Xposed framework privilege of current implementation.
     *
     * @return Framework privilege
     */
    int getFrameworkPrivilege();

    default Handler<?> hook(@NonNull Executable origin, @NonNull Pre injector) throws IllegalArgumentException {
        if (origin instanceof Method) {
            return hookMethod((Method) origin, injector);
        } else if (origin instanceof Constructor) {
            return hookConstructor((Constructor<?>) origin, injector);
        } else {
            throw new IllegalArgumentException("Unsupported origin type: " + origin.getClass().getName());
        }
    }
    default Handler<?> hook(@NonNull Executable origin, @NonNull Post injector) throws IllegalArgumentException {
        if (origin instanceof Method) {
            return hookMethod((Method) origin, injector);
        } else if (origin instanceof Constructor) {
            return hookConstructor((Constructor<?>) origin, injector);
        } else {
            throw new IllegalArgumentException("Unsupported origin type: " + origin.getClass().getName());
        }
    }
    default Handler<?> hook(@NonNull Executable origin, @NonNull Hook injector) throws IllegalArgumentException {
        if (origin instanceof Method) {
            return hookMethod((Method) origin, injector);
        } else if (origin instanceof Constructor) {
            return hookConstructor((Constructor<?>) origin, injector);
        } else {
            throw new IllegalArgumentException("Unsupported origin type: " + origin.getClass().getName());
        }
    }

    Handler<Method> hookMethod(@NonNull Method origin, @NonNull Pre injector);
    Handler<Method> hookMethod(@NonNull Method origin, @NonNull Post injector);
    Handler<Method> hookMethod(@NonNull Method origin, @NonNull Hook injector);

    /**
     * Hook a constructor with default priority.
     *
     * @param <T>    The type of the constructor
     * @param origin The constructor to be hooked
     * @param injector The injector
     * @return Handler for canceling the hook
     * @throws IllegalArgumentException if origin is abstract, framework internal or {@link Method#invoke},
     *                                  or hooker is invalid
     * @throws HookFailedError          if hook fails due to framework internal error
     */
    <T> Handler<Constructor<T>> hookConstructor(@NonNull Constructor<T> origin, @NonNull Pre injector);
    <T> Handler<Constructor<T>> hookConstructor(@NonNull Constructor<T> origin, @NonNull Post injector);
    <T> Handler<Constructor<T>> hookConstructor(@NonNull Constructor<T> origin, @NonNull Hook injector);

    /**
     * Deoptimizes a method in case hooked callee is not called because of inline.
     *
     * <p>By deoptimizing the method, the method will back all callee without inlining.
     * For example, when a short hooked method B is invoked by method A, the callback to B is not invoked
     * after hooking, which may mean A has inlined B inside its method body. To force A to call the hooked B,
     * you can deoptimize A and then your hook can take effect.</p>
     *
     * <p>Generally, you need to find all the callers of your hooked callee and that can be hardly achieve
     * (but you can still search all callers by using {@link DexParser}). Use this method if you are sure
     * the deoptimized callers are all you need. Otherwise, it would be better to change the hook point or
     * to deoptimize the whole app manually (by simply reinstalling the app without uninstall).</p>
     *
     * @param method The method to deoptimize
     * @return Indicate whether the deoptimizing succeed or not
     */
    boolean deoptimize(@NonNull Method method);

    /**
     * Deoptimizes a constructor in case hooked callee is not called because of inline.
     *
     * @param <T>         The type of the constructor
     * @param constructor The constructor to deoptimize
     * @return Indicate whether the deoptimizing succeed or not
     * @see #deoptimize(Method)
     */
    <T> boolean deoptimize(@NonNull Constructor<T> constructor);

    /**
     * Basically the same as {@link Method#invoke(Object, Object...)}, but calls the original method
     * as it was before the interception by Xposed.
     *
     * @param method     The method to be called
     * @param thisObject For non-static calls, the {@code this} pointer, otherwise {@code null}
     * @param args       The arguments used for the method call
     * @return The result returned from the invoked method
     * @see Method#invoke(Object, Object...)
     */
    @Nullable
    Object invokeOrigin(@NonNull Method method, @Nullable Object thisObject, Object... args) throws InvocationTargetException, IllegalArgumentException, IllegalAccessException;

    /**
     * Basically the same as {@link Constructor#newInstance(Object...)}, but calls the original constructor
     * as it was before the interception by Xposed.
     *
     * @param constructor The constructor to create and initialize a new instance
     * @param thisObject  The instance to be constructed
     * @param args        The arguments used for the construction
     * @param <T>         The type of the instance
     * @see Constructor#newInstance(Object...)
     */
    <T> void invokeOrigin(@NonNull Constructor<T> constructor, @NonNull T thisObject, Object... args) throws InvocationTargetException, IllegalArgumentException, IllegalAccessException;

    /**
     * Invokes a special (non-virtual) method on a given object instance, similar to the functionality of
     * {@code CallNonVirtual<type>Method} in JNI, which invokes an instance (nonstatic) method on a Java
     * object. This method is useful when you need to call a specific method on an object, bypassing any
     * overridden methods in subclasses and directly invoking the method defined in the specified class.
     *
     * <p>This method is useful when you need to call {@code super.xxx()} in a hooked constructor.</p>
     *
     * @param method     The method to be called
     * @param thisObject For non-static calls, the {@code this} pointer, otherwise {@code null}
     * @param args       The arguments used for the method call
     * @return The result returned from the invoked method
     * @see Method#invoke(Object, Object...)
     */
    @Nullable
    Object invokeSpecial(@NonNull Method method, @NonNull Object thisObject, Object... args) throws InvocationTargetException, IllegalArgumentException, IllegalAccessException;

    /**
     * Invokes a special (non-virtual) method on a given object instance, similar to the functionality of
     * {@code CallNonVirtual<type>Method} in JNI, which invokes an instance (nonstatic) method on a Java
     * object. This method is useful when you need to call a specific method on an object, bypassing any
     * overridden methods in subclasses and directly invoking the method defined in the specified class.
     *
     * <p>This method is useful when you need to call {@code super.xxx()} in a hooked constructor.</p>
     *
     * @param constructor The constructor to create and initialize a new instance
     * @param thisObject  The instance to be constructed
     * @param args        The arguments used for the construction
     * @see Constructor#newInstance(Object...)
     */
    <T> void invokeSpecial(@NonNull Constructor<T> constructor, @NonNull T thisObject, Object... args) throws InvocationTargetException, IllegalArgumentException, IllegalAccessException;

    /**
     * Basically the same as {@link Constructor#newInstance(Object...)}, but calls the original constructor
     * as it was before the interception by Xposed.
     *
     * @param <T>         The type of the constructor
     * @param constructor The constructor to create and initialize a new instance
     * @param args        The arguments used for the construction
     * @return The instance created and initialized by the constructor
     * @see Constructor#newInstance(Object...)
     */
    @NonNull
    <T> T newInstanceOrigin(@NonNull Constructor<T> constructor, Object... args) throws InvocationTargetException, IllegalArgumentException, IllegalAccessException, InstantiationException;

    /**
     * Creates a new instance of the given subclass, but initialize it with a parent constructor. This could
     * leave the object in an invalid state, where the subclass constructor are not called and the fields
     * of the subclass are not initialized.
     *
     * <p>This method is useful when you need to initialize some fields in the subclass by yourself.</p>
     *
     * @param <T>         The type of the parent constructor
     * @param <U>         The type of the subclass
     * @param constructor The parent constructor to initialize a new instance
     * @param subClass    The subclass to create a new instance
     * @param args        The arguments used for the construction
     * @return The instance of subclass initialized by the constructor
     * @see Constructor#newInstance(Object...)
     */
    @NonNull
    <T, U> U newInstanceSpecial(@NonNull Constructor<T> constructor, @NonNull Class<U> subClass, Object... args) throws InvocationTargetException, IllegalArgumentException, IllegalAccessException, InstantiationException;

    /**
     * Writes a message to the Xposed log.
     *
     * @param message The log message
     */
    void log(@NonNull String message);

    /**
     * Writes a message with a stack trace to the Xposed log.
     *
     * @param message   The log message
     * @param throwable The Throwable object for the stack trace
     */
    void log(@NonNull String message, @NonNull Throwable throwable);

    /**
     * Parse a dex file in memory.
     *
     * @param dexData            The content of the dex file
     * @param includeAnnotations Whether to include annotations
     * @return The {@link DexParser} of the dex file
     * @throws IOException if the dex file is invalid
     */
    @Nullable
    DexParser parseDex(@NonNull ByteBuffer dexData, boolean includeAnnotations) throws IOException;

    /**
     * Gets the application info of the module.
     */
    @NonNull
    ApplicationInfo getApplicationInfo();

    /**
     * Gets remote preferences stored in Xposed framework. Note that those are read-only in hooked apps.
     *
     * @param group Group name
     * @return The preferences
     * @throws UnsupportedOperationException If the framework is embedded
     */
    @NonNull
    SharedPreferences getRemotePreferences(@NonNull String group);

    /**
     * List all files in the module's shared data directory.
     *
     * @return The file list
     * @throws UnsupportedOperationException If the framework is embedded
     */
    @NonNull
    String[] listRemoteFiles();

    /**
     * Open a file in the module's shared data directory. The file is opened in read-only mode.
     *
     * @param name File name, must not contain path separators and . or ..
     * @return The file descriptor
     * @throws FileNotFoundException         If the file does not exist or the path is forbidden
     * @throws UnsupportedOperationException If the framework is embedded
     */
    @NonNull
    ParcelFileDescriptor openRemoteFile(@NonNull String name) throws FileNotFoundException;


    /**
     * Wrap of {@link XposedInterface} used by the modules for the purpose of shielding framework implementation details.
     */
    @SuppressWarnings("unused")
    class Wrapper implements XposedInterface {

        private final XposedInterface mBase;

        Wrapper(@NonNull XposedInterface base) {
            mBase = base;
        }

        @NonNull
        @Override
        public final String getFrameworkName() {
            return mBase.getFrameworkName();
        }

        @NonNull
        @Override
        public final String getFrameworkVersion() {
            return mBase.getFrameworkVersion();
        }

        @Override
        public final long getFrameworkVersionCode() {
            return mBase.getFrameworkVersionCode();
        }

        @Override
        public final int getFrameworkPrivilege() {
            return mBase.getFrameworkPrivilege();
        }

        @Override
        public final Handler<Method> hookMethod(@NonNull Method origin, @NonNull Pre injector) {
            return mBase.hookMethod(origin, injector);
        }
        @Override
        public final Handler<Method> hookMethod(@NonNull Method origin, @NonNull Post injector) {
            return mBase.hookMethod(origin, injector);
        }
        @Override
        public final Handler<Method> hookMethod(@NonNull Method origin, @NonNull Hook injector) {
            return mBase.hookMethod(origin, injector);
        }

        @Override
        public final <T> Handler<Constructor<T>> hookConstructor(@NonNull Constructor<T> origin, @NonNull Pre injector) {
            return mBase.hookConstructor(origin, injector);
        }
        @Override
        public final <T> Handler<Constructor<T>> hookConstructor(@NonNull Constructor<T> origin, @NonNull Post injector) {
            return mBase.hookConstructor(origin, injector);
        }
        @Override
        public final <T> Handler<Constructor<T>> hookConstructor(@NonNull Constructor<T> origin, @NonNull Hook injector) {
            return mBase.hookConstructor(origin, injector);
        }

        @Override
        public final boolean deoptimize(@NonNull Method method) {
            return mBase.deoptimize(method);
        }

        @Override
        public final <T> boolean deoptimize(@NonNull Constructor<T> constructor) {
            return mBase.deoptimize(constructor);
        }

        @Nullable
        @Override
        public final Object invokeOrigin(@NonNull Method method, @Nullable Object thisObject, Object... args) throws InvocationTargetException, IllegalArgumentException, IllegalAccessException {
            return mBase.invokeOrigin(method, thisObject, args);
        }

        @Override
        public final <T> void invokeOrigin(@NonNull Constructor<T> constructor, @NonNull T thisObject, Object... args) throws InvocationTargetException, IllegalArgumentException, IllegalAccessException {
            mBase.invokeOrigin(constructor, thisObject, args);
        }

        @Nullable
        @Override
        public final Object invokeSpecial(@NonNull Method method, @NonNull Object thisObject, Object... args) throws InvocationTargetException, IllegalArgumentException, IllegalAccessException {
            return mBase.invokeSpecial(method, thisObject, args);
        }

        @Override
        public final <T> void invokeSpecial(@NonNull Constructor<T> constructor, @NonNull T thisObject, Object... args) throws InvocationTargetException, IllegalArgumentException, IllegalAccessException {
            mBase.invokeSpecial(constructor, thisObject, args);
        }

        @NonNull
        @Override
        public final <T> T newInstanceOrigin(@NonNull Constructor<T> constructor, Object... args) throws InvocationTargetException, IllegalArgumentException, IllegalAccessException, InstantiationException {
            return mBase.newInstanceOrigin(constructor, args);
        }

        @NonNull
        @Override
        public final <T, U> U newInstanceSpecial(@NonNull Constructor<T> constructor, @NonNull Class<U> subClass, Object... args) throws InvocationTargetException, IllegalArgumentException, IllegalAccessException, InstantiationException {
            return mBase.newInstanceSpecial(constructor, subClass, args);
        }

        @Override
        public final void log(@NonNull String message) {
            mBase.log(message);
        }

        @Override
        public final void log(@NonNull String message, @NonNull Throwable throwable) {
            mBase.log(message, throwable);
        }

        @Nullable
        @Override
        public final DexParser parseDex(@NonNull ByteBuffer dexData, boolean includeAnnotations) throws IOException {
            return mBase.parseDex(dexData, includeAnnotations);
        }

        @NonNull
        @Override
        public final SharedPreferences getRemotePreferences(@NonNull String name) {
            return mBase.getRemotePreferences(name);
        }

        @NonNull
        @Override
        public final ApplicationInfo getApplicationInfo() {
            return mBase.getApplicationInfo();
        }

        @NonNull
        @Override
        public final String[] listRemoteFiles() {
            return mBase.listRemoteFiles();
        }

        @NonNull
        @Override
        public final ParcelFileDescriptor openRemoteFile(@NonNull String name) throws FileNotFoundException {
            return mBase.openRemoteFile(name);
        }
    }
}
