package io.github.libxposed.api;

@SuppressWarnings("unused")
public interface Stateful {

    /**
     * Gets the state of the hook.
     *
     * @return The state of the hook
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
