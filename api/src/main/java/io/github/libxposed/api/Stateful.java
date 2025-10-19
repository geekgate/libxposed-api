package io.github.libxposed.api;

@SuppressWarnings("unused")
public interface Stateful {

    /**
     * Indicate whether the injector is ready.
     *
     * @return true if the injector is ready, false otherwise.
     */
    boolean isReady();
    /**
     * Enables the hook if it is a Stateful hook.
     */
    void enable();
    /**
     * Disables the hook if it is a Stateful hook.
     */
    void disable();

    enum State {
        Undefined, Ready,
    }

    interface Default extends Stateful {

        State getState();
        void setState(State state);

        @Override
        default boolean isReady() {
            return getState() == State.Ready;
        }
        @Override
        default void enable() {
            setState(State.Ready);
        }
        @Override
        default void disable() {
            setState(State.Undefined);
        }
    }
}
