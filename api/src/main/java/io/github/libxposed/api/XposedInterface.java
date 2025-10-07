package io.github.libxposed.api;

import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.res.Resources;

import androidx.annotation.Discouraged;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.ConcurrentModificationException;

import io.github.libxposed.api.errors.HookFailedError;
import io.github.libxposed.api.utils.DexParser;

/**
 * Xposed interface for modules to operate on application processes.
 */
@SuppressWarnings("unused")
public interface XposedInterface {
    /**
     * SDK API version.
     */
    int API = 101;

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
     * which means {@link #getSharedPreferences} will be null and remote file is unsupported.
     */
    int FRAMEWORK_PRIVILEGE_EMBEDDED = 3;

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

    interface Logger {
        /**
         * Logs an info message.
         * @param args the args
         */
        void i(Object ... args);
        /**
         * Logs a warning message.
         * @param args the args
         */
        void w(Object ... args);
        /**
         * Logs an error message.
         * @param args the args
         */
        void e(Object ... args);
        /**
         * Logs an error message.
         * @param message the message
         * @param t the throwable
         */
        void e(String  message, Throwable t);
        /**
         * Logs a debug message.
         * @param args the args
         */
        void d(Object ... args);
        /**
         * Logs a verbose message.
         * @param args the args
         */
        void v(Object ... args);
        /**
         * Logs a bare message without a tag.
         * @param args the args
         */
        void z(Object ... args);
    }

    interface Context {
        /**
         * Gets original method or constructor.
         *
         * @return the original method or constructor.
         */
        @NonNull
        Executable getOrigin();
        /**
         * Gets this.
         *
         * @return the this
         */
        @Nullable
        Object getThis();
        /**
         * Invoke original object.
         *
         * @return the object
         * @throws InvocationTargetException the invocation target exception
         * @throws IllegalArgumentException  the illegal argument exception
         * @throws IllegalAccessException    the illegal access exception
         */
        @Nullable
        Object invokeOrigin() throws InvocationTargetException, IllegalArgumentException, IllegalAccessException;
        /**
         * Load a class in the hooked app.
         * @param className the class name
         * @return the class
         * @throws ClassNotFoundException if the class cannot be located
         */
        Class<?> loadClass(@NonNull String className) throws ClassNotFoundException;
        /**
         * Gets log.
         * @return the log
         */
        Logger getLogger();
    }

    /**
     * The interface Before hook callback.
     *
     */
    interface BeforeHookContext extends Context {

        /**
         * Return and skip.
         *
         * @param returnValue the return value
         */
        void returnAndSkip(@Nullable Object returnValue);

        /**
         * Throw and skip.
         *
         * @param throwable the throwable
         */
        void throwAndSkip(@Nullable Throwable throwable);

        /**
         * Sets extra.
         *
         * @param <U>   the type parameter
         * @param key   the key
         * @param value the value
         * @throws ConcurrentModificationException the concurrent modification exception
         */
        <U> void setExtra(@NonNull String key, @Nullable U value) throws ConcurrentModificationException;
    }

    /**
     * The interface After hook callback.
     *
     */
    interface AfterHookContext extends Context {

        /**
         * Get args object [ ].
         *
         * @return the object [ ]
         */
        @NonNull
        Object[] getArgs();

        /**
         * Gets result.
         *
         * @return the result
         */
        @Nullable
        Object getResult();

        /**
         * Gets throwable.
         *
         * @return the throwable
         */
        @Nullable
        Throwable getThrowable();

        /**
         * Is skipped boolean.
         *
         * @return the boolean
         */
        boolean isSkipped();

        /**
         * Sets result.
         *
         * @param result the result
         */
        void setResult(@Nullable Object result);

        /**
         * Sets throwable.
         *
         * @param throwable the throwable
         */
        void setThrowable(@Nullable Throwable throwable);

        /**
         * Gets extra.
         *
         * @param <U> the type parameter
         * @param key the key
         * @return the extra
         */
        @Nullable
        <U> U getExtra(@NonNull String key);
    }

    /**
     * The interface Injector. Find the injection method based on the method signature.
     */
    interface Injector { }

    /**
     * The interface Before hooker.
     *
     */
    @FunctionalInterface
    interface PreInjector extends Injector {
        /**
         * Before.
         *
         * @param context the context
         * @param args the args
         */
        void inject(@NonNull BeforeHookContext context, Object[] args);
    }

    /**
     * The interface After hooker.
     *
     */
    @FunctionalInterface
    interface PostInjector extends Injector {
        /**
         * After.
         *
         * @param context the context
         */
        void inject(@NonNull AfterHookContext context, Object returnValue, Throwable throwable);
    }

    /**
     * The interface Hooker.
     *
     */
    interface Hook extends PreInjector, PostInjector {
    }

    /**
     * The interface Method unhooker.
     *
     */
    interface Unhooker {
        /**
         * Gets origin method/constructor.
         *
         * @return the origin
         */
        @NonNull
        Executable getOrigin();

        /**
         * Gets injector.
         *
         * @return the injector
         */
        @NonNull
        Injector getInjector();

        /**
         * Unhook.
         */
        void unhook();
    }

    /**
     * Get the Xposed framework name of current implementation.
     *
     * @return Framework name
     */
    @NonNull
    String getFrameworkName();

    /**
     * Get the Xposed framework version of current implementation.
     *
     * @return Framework version
     */
    @NonNull
    String getFrameworkVersion();

    /**
     * Get the Xposed framework version code of current implementation.
     *
     * @return Framework version code
     */
    long getFrameworkVersionCode();

    /**
     * Get the Xposed framework privilege of current implementation.
     *
     * @return Framework privilege
     */
    int getFrameworkPrivilege();

    /**
     * Additional methods provided by specific Xposed framework.
     *
     * @param name Featured method name
     * @param args Featured method arguments
     * @return Featured method result
     * @throws UnsupportedOperationException If the framework does not provide a method with given name
     */
    @Discouraged(message = "Normally, modules should never rely on specific implementation of the Xposed framework. But if really necessary, this method can be used to acquire such information.")
    @Nullable
    Object featuredMethod(String name, Object... args);

    /**
     * Hook method unhooker.
     *
     * @param origin the origin method
     * @param injector the hooker
     * @return the method unhooker
     * @throws IllegalArgumentException if origin is abstract, framework internal or {@link Method#invoke}
     * @throws HookFailedError          if hook fails due to framework internal error
     */
    @NonNull
    Unhooker hook(@NonNull Method origin, @NonNull Injector injector);

    /**
     * Hook method unhooker.
     *
     * @param origin   the origin method
     * @param priority the priority
     * @param injector   the hooker
     * @return the method unhooker
     * @throws IllegalArgumentException if origin is abstract, framework internal or {@link Method#invoke}
     * @throws HookFailedError          if hook fails due to framework internal error
     */
    @NonNull
    Unhooker hook(@NonNull Method origin, int priority, @NonNull Injector injector);
    /**
     * Hook method unhooker.
     *
     * @param origin the origin constructor
     * @param injector the hooker
     * @return the method unhooker
     * @throws IllegalArgumentException if origin is abstract, framework internal or {@link Method#invoke}
     * @throws HookFailedError          if hook fails due to framework internal error
     */
    @NonNull
    Unhooker hook(@NonNull Constructor<?> origin, @NonNull Injector injector);

    /**
     * Hook method unhooker.
     *
     * @param origin   the origin constructor
     * @param priority the priority
     * @param injector   the hooker
     * @return the method unhooker
     * @throws IllegalArgumentException if origin is abstract, framework internal or {@link Method#invoke}
     * @throws HookFailedError          if hook fails due to framework internal error
     */
    @NonNull
    Unhooker hook(@NonNull Constructor<?> origin, int priority, @NonNull Injector injector);

    /**
     * Deoptimize boolean.
     *
     * @param method the method
     * @return the boolean
     */
    boolean deoptimize(@NonNull Method method);

    /**
     * Deoptimize boolean.
     *
     * @param <T>         the type parameter
     * @param constructor the constructor
     * @return the boolean
     */
    <T> boolean deoptimize(@NonNull Constructor<T> constructor);

    /**
     * Invoke origin object.
     *
     * @param method     the method
     * @param thisObject the this object
     * @param args       the args
     * @return the object
     * @throws InvocationTargetException the invocation target exception
     * @throws IllegalArgumentException  the illegal argument exception
     * @throws IllegalAccessException    the illegal access exception
     */
    @Nullable
    Object invokeOrigin(@NonNull Method method, @Nullable Object thisObject, Object... args) throws InvocationTargetException, IllegalArgumentException, IllegalAccessException;

    /**
     * Invoke special object.
     *
     * @param method     the method
     * @param thisObject the this object
     * @param args       the args
     * @return the object
     * @throws InvocationTargetException the invocation target exception
     * @throws IllegalArgumentException  the illegal argument exception
     * @throws IllegalAccessException    the illegal access exception
     */
    @Nullable
    Object invokeSpecial(@NonNull Method method, @NonNull Object thisObject, Object... args) throws InvocationTargetException, IllegalArgumentException, IllegalAccessException;

    /**
     * new origin object.
     *
     * @param <T>         the type parameter
     * @param constructor the constructor
     * @param args        the args
     * @return the object
     * @throws InvocationTargetException the invocation target exception
     * @throws IllegalArgumentException  the illegal argument exception
     * @throws IllegalAccessException    the illegal access exception
     * @throws InstantiationException    the instantiation exception
     */
    @NonNull
    <T> T newInstanceOrigin(@NonNull Constructor<T> constructor, Object... args) throws InvocationTargetException, IllegalArgumentException, IllegalAccessException, InstantiationException;

    /**
     * New instance special u.
     *
     * @param <T>         the type parameter
     * @param <U>         the type parameter
     * @param constructor the constructor
     * @param subClass    the sub class
     * @param args        the args
     * @return the u
     * @throws InvocationTargetException the invocation target exception
     * @throws IllegalArgumentException  the illegal argument exception
     * @throws IllegalAccessException    the illegal access exception
     * @throws InstantiationException    the instantiation exception
     */
    @NonNull
    <T, U> U newInstanceSpecial(@NonNull Constructor<T> constructor, @NonNull Class<U> subClass, Object... args) throws InvocationTargetException, IllegalArgumentException, IllegalAccessException, InstantiationException;

    /**
     * Log.
     *
     * @param message the message
     */
    void log(@NonNull String message);

    /**
     * Log.
     *
     * @param message   the message
     * @param throwable the throwable
     */
    void log(@NonNull String message, @NonNull Throwable throwable);

    /**
     * Parse dex dex parser.
     *
     * @param dexData            the dex data
     * @param includeAnnotations the include annotations
     * @return the dex parser
     * @throws IOException the io exception
     */
    @Nullable
    DexParser parseDex(@NonNull ByteBuffer dexData, boolean includeAnnotations) throws IOException;


    // Methods the same with Context

    /**
     * Gets shared preferences.
     *
     * @param name the name
     * @param mode the mode
     * @return the shared preferences
     */
    SharedPreferences getSharedPreferences(String name, int mode);

    /**
     * Open file input file input stream.
     *
     * @param name the name
     * @return the file input stream
     * @throws FileNotFoundException the file not found exception
     */
    FileInputStream openFileInput(String name) throws FileNotFoundException;

    /**
     * File list string [ ].
     *
     * @return the string [ ]
     */
    String[] fileList();

    /**
     * Gets resources.
     *
     * @return the resources
     */
    Resources getResources();

    /**
     * Gets class loader.
     *
     * @return the class loader
     */
    ClassLoader getClassLoader();

    /**
     * Gets application info.
     *
     * @return the application info
     */
    ApplicationInfo getApplicationInfo();

    PackageInfo getPackageInfo();

    android.content.Context getSystemContext();
}
