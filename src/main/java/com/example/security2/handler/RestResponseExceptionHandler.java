package com.example.security2.handler;

import com.example.security2.dto.ErrorDto;
import com.example.security2.exception.DuplicateMemberException;
import com.example.security2.exception.NotFoundMemberException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.nio.file.AccessDeniedException;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@RestControllerAdvice
public class RestResponseExceptionHandler extends ResponseEntityExceptionHandler {

    @ResponseStatus(CONFLICT)
    @ExceptionHandler(value = { DuplicateMemberException.class })
    protected ErrorDto conflict(RuntimeException ex, WebRequest request) {
        return new ErrorDto(CONFLICT.value(), ex.getMessage());
    }

    @ResponseStatus(FORBIDDEN)
    @ExceptionHandler(value = { NotFoundMemberException.class, AccessDeniedException.class })
    protected ErrorDto forbidden(RuntimeException ex, WebRequest request) {
        return new ErrorDto(FORBIDDEN.value(), ex.getMessage());
    }
}
