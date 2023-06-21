package com.reactivespring.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<String> handBindException(WebExchangeBindException exception){
        log.error("Caught exception: ", exception.getMessage(), exception);
        var error = exception.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
                .sorted().collect(Collectors.joining(","));
        log.error("Error message: "+error);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
