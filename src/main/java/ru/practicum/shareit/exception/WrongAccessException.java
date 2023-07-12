package ru.practicum.shareit.exception;


public class WrongAccessException extends RuntimeException {
    public WrongAccessException(String message) {
        super(message);
    }
}