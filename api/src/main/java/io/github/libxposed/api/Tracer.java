package io.github.libxposed.api;


import android.util.Log;
import androidx.annotation.NonNull;

/**
 * Tracer-injector
 */
public class Tracer implements Post<Tracer.MyContext> {
    private final String tag;
    private final String entry;

    public Tracer() {
        this.tag = "LSPosed";
        this.entry = null;
    }
    public Tracer(String tag) {
        this.tag = tag;
        this.entry = null;
    }
    public Tracer(String tag, String entry) {
        this.tag = tag;
        this.entry = entry;
    }

    abstract public static class MyContext implements Context {
        public MyContext(Object target) {

        }
    }

    @Override
    public void inject(@NonNull MyContext context, Object result, Throwable throwable) {
        var entry = this.entry;
        if (entry == null || entry.isEmpty()) {
            entry = context.getTarget().toString();
        }
        Log.i(tag, entry + " injected");
    }
}