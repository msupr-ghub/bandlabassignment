package com.bandlab.assignment.exceptionhandler;

import com.bandlab.assignment.api.response.GenericApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.logging.Logger;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GenericApiResponse<String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error("Method argument not valid: ", ex);
        return new ResponseEntity<>(GenericApiResponse.<String>builder()
                .message("Invalid request parameters").build(),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GenericApiResponse<String>> handlePostNotFoundException(Exception ex) {
        log.error("Exception occurred: ", ex);
        return new ResponseEntity<>(GenericApiResponse.<String>builder()
                .message("There was some issue processing your request, please try again later").build(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
