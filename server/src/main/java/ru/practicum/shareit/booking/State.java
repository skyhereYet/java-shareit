package ru.practicum.shareit.booking;

import ru.practicum.shareit.exception.StateCheckException;

public enum State {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static State checkState(String stateDto) {
        for (State stateEnum : State.values()) {
            if (stateDto.equals(stateEnum.toString())) {
                return stateEnum;
            }
        }
        throw new StateCheckException("Unknown state: " + stateDto);
    }
}