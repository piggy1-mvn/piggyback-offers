package com.incentives.piggyback.offers.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidRequestException extends RuntimeException  {

    public InvalidRequestException( String errorMessage) {
        super(errorMessage);
    }
}