package ru.practicum.shareit.create;

import ru.practicum.shareit.exception.UnsupportedStatusException;

public enum State {

    ALL,

    CURRENT,

    PAST,

    FUTURE,

    WAITING,

    REJECTED;

    public static State getEnumValue(String state) {
        if ((state == null) || state.isBlank()) {
            return State.ALL;
        }
        try {
            return State.valueOf(state.toUpperCase().trim());
        } catch (Exception e) {
            throw new UnsupportedStatusException(String.format("Unknown state: %s", state));
        }
    }
}