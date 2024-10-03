package edu.shtoiko.authservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ResponseException extends RuntimeException {

    private final HttpStatus status;

    public ResponseException(int statusCode, String message) {
        super(message);
        status = HttpStatus.valueOf(statusCode);
    }

    public ResponseException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
}
