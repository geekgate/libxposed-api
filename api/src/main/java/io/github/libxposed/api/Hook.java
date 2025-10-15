package io.github.libxposed.api;

import androidx.annotation.NonNull;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("unused")
public interface Hook extends Pre, Post {

    @Override
    default void inject(@NonNull Pre.Context ctx, @NonNull Object[] args) {}

    @Override
    default void inject(@NonNull Post.Context ctx, Object result, Throwable throwable) {}

    abstract class Base implements Hook, Stateful.Default {
        public int priority;
        private final AtomicReference<State> state = new AtomicReference<>(State.Ready);

        public Base(int priority) {
            this.priority = priority;
        }

        @Override
        public int getPriority() {
            return priority;
        }

        @Override
        public State getState() {
            return state.get();
        }

        @Override
        public void setState(State state) {
            this.state.set(state);
        }

        public void highest() {
            priority = Injector.PRIORITY_HIGHEST;
        }
        public void lowest() {
            priority = Injector.PRIORITY_LOWEST;
        }
    }

    class Combined extends Base {
        private final Pre pre;
        private final Post post;
        public Combined(Pre pre, Post post, int priority) {
            super(priority);

            this.pre = pre;
            this.post = post;
        }
        public Combined(Pre pre, Post post) {
            this(pre, post, Injector.PRIORITY_DEFAULT);
        }

        @Override
        public void inject(@NonNull Pre.Context ctx, @NonNull Object[] args) {
            pre.inject(ctx, args);
        }
        @Override
        public void inject(@NonNull Post.Context ctx, Object result, Throwable throwable) {
            post.inject(ctx, result, throwable);
        }
    }

    class Counted extends Combined implements Countable {
        private final AtomicLong count = new AtomicLong(0L);

        public Counted(Pre pre, Post post, int priority) {
            super(pre, post, priority);
        }

        @Override
        public void inc() {
            count.incrementAndGet();
        }

        @Override
        public void dec() {
            count.decrementAndGet();
        }

        @Override
        public void reset() {
            count.set(0L);
        }

        @Override
        public Long count() {
            return count.get();
        }

        @Override
        public Long incAndGet() {
            return count.incrementAndGet();
        }

        @Override
        public Long decAndGet() {
            return count.decrementAndGet();
        }

        @Override
        public void done(Injector.Context ctx) {
            count.incrementAndGet();
        }
    }
}
