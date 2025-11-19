package com.yana.filestorage.exception;

import com.yana.filestorage.api.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class BaseExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(DirectoryActionException.class)
    public final ResponseEntity<ErrorResponse> handleFileActionException(DirectoryActionException ex) {
        log.error(ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(FileActionException.class)
    public final ResponseEntity<ErrorResponse> handleFileActionException(FileActionException ex) {
        log.error(ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(UserNotAuthenticatedException.class)
    public final ResponseEntity<ErrorResponse> handleFileActionException(UserNotAuthenticatedException ex) {
        log.error(ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .message(ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public final ResponseEntity<ErrorResponse> handleFileActionException(AccessDeniedException ex) {
        log.error(ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .message(ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler(DirectoryNotFoundException.class)
    public final ResponseEntity<ErrorResponse> handleFileActionException(DirectoryNotFoundException ex) {
        log.error(ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(FileNotFoundException.class)
    public final ResponseEntity<ErrorResponse> handleFileActionException(FileNotFoundException ex) {
        log.error(ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(FileAlreadyExistsException.class)
    public final ResponseEntity<ErrorResponse> handleFileActionException(FileAlreadyExistsException ex) {
        log.error(ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse()
                .statusCode(HttpStatus.CONFLICT.value())
                .message(ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(DirectoryAlreadyExists.class)
    public final ResponseEntity<ErrorResponse> handleFileActionException(DirectoryAlreadyExists ex) {
        log.error(ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse()
                .statusCode(HttpStatus.CONFLICT.value())
                .message(ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler({Throwable.class})
    public ResponseEntity<ErrorResponse> handleException(Throwable ex) {
        log.error(ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

}
