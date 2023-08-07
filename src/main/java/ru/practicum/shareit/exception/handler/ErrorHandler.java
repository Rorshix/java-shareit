package ru.practicum.shareit.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.*;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final ValidationException e) {
        String error = String.format("validator exception: %s", e.getMessage());
        log.info(error);
        return new ErrorResponse(error);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        String error = String.format("not found exception: %s", e.getMessage());
        log.info(error);
        return new ErrorResponse(error);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleEmailExistException(final WrongAccessException e) {
        String error = String.format("conflict exception: %s", e.getMessage());
        log.info(error);
        return new ErrorResponse(error);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse serverError(final Throwable e) {
       String error = String.format("server error: %s", e.getMessage());
       log.info(error);
       return new ErrorResponse(error);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        String error = e.getMessage();
        String string = "default message";
        int index = error.lastIndexOf(string);
        String strMessage = index == 0 ? "" : error.substring(index + string.length());
        error = String.format("Method argument not valid: %s", strMessage.isBlank() ? error : strMessage);
        log.info(error);
        return new ErrorResponse(error);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUnsupportedStatusException(final UnsupportedStatusException e) {
        String error = String.format("Unknown state: UNSUPPORTED_STATUS", e.getMessage());
        log.info(error);
        return new ErrorResponse(error);
    }
}