package com.yana.filestorage.exception;

import com.yana.filestorage.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class BaseExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public final ResponseEntity<ErrorResponse> handleFileActionException(AccessDeniedException ex) {
        log.error(ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.FORBIDDEN.value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(errorResponse.statusCode()));
    }

    @ExceptionHandler(UserNotAuthenticatedException.class)
    public final ResponseEntity<ErrorResponse> handleFileActionException(UserNotAuthenticatedException ex) {
        log.error(ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(errorResponse.statusCode()));
    }

    @ExceptionHandler(FileAlreadyExistsException.class)
    public final ResponseEntity<ErrorResponse> handleFileActionException(FileAlreadyExistsException ex) {
        log.error(ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.CONFLICT.value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(errorResponse.statusCode()));
    }

    @ExceptionHandler(DirectoryAlreadyExists.class)
    public final ResponseEntity<ErrorResponse> handleFileActionException(DirectoryAlreadyExists ex) {
        log.error(ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.CONFLICT.value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(errorResponse.statusCode()));
    }

    @ExceptionHandler(DirectoryNotFoundException.class)
    public final ResponseEntity<ErrorResponse> handleFileActionException(DirectoryNotFoundException ex) {
        log.error(ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(errorResponse.statusCode()));
    }

    @ExceptionHandler(FileNotFoundException.class)
    public final ResponseEntity<ErrorResponse> handleFileActionException(FileNotFoundException ex) {
        log.error(ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(errorResponse.statusCode()));
    }

    @ExceptionHandler(DirectoryActionException.class)
    public final ResponseEntity<ErrorResponse> handleFileActionException(DirectoryActionException ex) {
        log.error(ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(errorResponse.statusCode()));
    }

    @ExceptionHandler(FileActionException.class)
    public final ResponseEntity<ErrorResponse> handleFileActionException(FileActionException ex) {
        log.error(ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(errorResponse.statusCode()));
    }

    @ExceptionHandler({Throwable.class})
    public ResponseEntity<ErrorResponse> handleException(Throwable ex) {
        log.error(ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(errorResponse.statusCode()));
    }

}
