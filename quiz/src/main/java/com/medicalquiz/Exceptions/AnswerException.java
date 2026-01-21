package com.medicalquiz.Exceptions;

public class AnswerException extends RuntimeException{
    public AnswerException(String message) {
        super(message);
    }
    public AnswerException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
