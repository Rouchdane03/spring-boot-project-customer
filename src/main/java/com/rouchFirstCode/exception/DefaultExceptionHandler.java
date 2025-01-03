package com.rouchFirstCode.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice //conseille le controller pour la gestion des exceptions
public class DefaultExceptionHandler {


    @ExceptionHandler(ResourceNotFoundException.class) //pour plusieurs exceptions on fait : {} dans la parenthèse
    public ResponseEntity<ApiError> handleException(ResourceNotFoundException resourceNotFoundException,
                                                    HttpServletRequest request){
        ApiError apiError = new ApiError(
                request.getRequestURI(),
                resourceNotFoundException.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ResponseEntity<ApiError> handleException(InsufficientAuthenticationException insufficientAuthenticationException,
                                                    HttpServletRequest request){
        ApiError apiError = new ApiError(
                request.getRequestURI(),
                insufficientAuthenticationException.getMessage(),
                HttpStatus.FORBIDDEN.value(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(apiError, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleException(BadCredentialsException badCredentialsException,
                                                    HttpServletRequest request){
        ApiError apiError = new ApiError(
                request.getRequestURI(),
                badCredentialsException.getMessage(),
                HttpStatus.UNAUTHORIZED.value(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleException(Exception exception,
                                                    HttpServletRequest request){
        ApiError apiError = new ApiError(
                request.getRequestURI(),
                exception.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
