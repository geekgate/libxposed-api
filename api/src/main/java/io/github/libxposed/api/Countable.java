package io.github.libxposed.api;

public interface Countable {
    void inc();
    void dec();
    void reset();
    Long count();
    Long incAndGet();
    Long decAndGet();
}
