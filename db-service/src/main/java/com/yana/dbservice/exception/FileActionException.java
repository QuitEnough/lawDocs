package com.yana.dbservice.exception;

public class FileActionException extends RuntimeException {

    public FileActionException(String message) {
        super(message);
    }

    public FileActionException(String message, Throwable cause) {
        super(message, cause);
    } // remove

}
