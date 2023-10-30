package br.com.solutis.squad1.gateway.exception;

import lombok.Getter;

@Getter
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
