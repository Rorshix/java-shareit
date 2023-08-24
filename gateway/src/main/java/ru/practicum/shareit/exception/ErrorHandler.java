package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleUnsupportedStatusException(final UnsupportedStatusException e) {
        log.debug("Получен статус 500 {}", e.getMessage(), e);
        return new ErrorResponse("Unknown state: UNSUPPORTED_STATUS");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final ValidationException e) {
        log.debug("Получен статус 400 {}", e.getMessage(), e);
        return new ErrorResponse("Validation exception");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        log.debug("Получен статус 404 {}", e.getMessage(), e);
        return new ErrorResponse("Not found exception");
    }
}