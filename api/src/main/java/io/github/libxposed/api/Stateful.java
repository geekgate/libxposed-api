package io.github.libxposed.api;

public interface Stateful {
    enum State {
        Undefined, Ready, Done,
    }
    State getState();
    void setState(State state);
    boolean isReady();
}
