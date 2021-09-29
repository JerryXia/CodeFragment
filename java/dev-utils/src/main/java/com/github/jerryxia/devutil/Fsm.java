package com.github.jerryxia.devutil;

import java.util.HashMap;
import java.util.function.Consumer;

/**
 * @author Administrator
 * @date 2021/09/29
 */
public class Fsm<T> {
    private final HashMap<String/*state,event*/, TransitionTable<T>> regTable = new HashMap<String, TransitionTable<T>>();
    /**
     * current state
     */
    private Enum<?> state;

    public Fsm(Enum<?> state) {
        if (state == null) {
            throw new IllegalArgumentException("state can't be null.");
        }
        this.state = state;
    }

    public Fsm configure(Enum<?> sourceState, Enum<?> triggerEvent, Enum<?> destinationState, Consumer<T> action) {
        if (sourceState == null) {
            throw new IllegalArgumentException("sourceState can't be null.");
        }
        if (triggerEvent == null) {
            throw new IllegalArgumentException("triggerEvent can't be null.");
        }
        if (destinationState == null) {
            throw new IllegalArgumentException("destinationState can't be null.");
        }
        regTable.put(sourceState.name() + "," + triggerEvent.name(),
                new TransitionTable<T>(sourceState, triggerEvent, destinationState, action));
        return this;
    }

    public void fire(Enum<?> triggerEvent, T eventValue) {
        TransitionTable<T> transitionTable = regTable.get(this.state.name() + "," + triggerEvent.name());
        Consumer<T> action = transitionTable.getAction();
        if (action == null) {
            action = NoOPAction.INSTANCE;
        }
        action.accept(eventValue);
        this.state = transitionTable.getDestinationState();
    }

    public <E extends Enum<E>> E getState() {
        return (E) this.state;
    }

    class TransitionTable<T> {
        private final Enum<?> sourceState;
        private final Enum<?> triggerEvent;
        private final Enum<?> destinationState;
        private final Consumer<T> action;

        public TransitionTable(Enum<?> sourceState, Enum<?> triggerEvent, Enum<?> destinationState, Consumer<T> action) {
            this.sourceState = sourceState;
            this.triggerEvent = triggerEvent;
            this.destinationState = destinationState;
            this.action = action;
        }

        public Enum<?> getSourceState() {
            return sourceState;
        }

        public Enum<?> getTriggerEvent() {
            return triggerEvent;
        }

        public Enum<?> getDestinationState() {
            return destinationState;
        }

        public Consumer<T> getAction() {
            return action;
        }
    }
}
