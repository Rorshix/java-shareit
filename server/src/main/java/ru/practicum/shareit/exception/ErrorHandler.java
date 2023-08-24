package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

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

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleEmailExistException(final EmailExistException e) {
        log.debug("Получен статус 409 {}", e.getMessage(), e);
        return new ErrorResponse("Email exception");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse serverError(final Throwable e) {
        log.debug("Получен статус 500 {}", e.getMessage(), e);
        return new ErrorResponse("Unsupported status exception");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.debug("Получен статус 400 {}", e.getMessage(), e);
        return new ErrorResponse("Validation exception");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUnsupportedStatusException(final UnsupportedStatusException e) {
        log.debug("Получен статус 400 {}", e.getMessage(), e);
        return new ErrorResponse("Unknown state: UNSUPPORTED_STATUS");
    }
}