package com.bakuard.flashcards.validation;

public class UnknownEntityException extends RuntimeException {

    private String messageKey;
    private boolean internalServerException;

    public UnknownEntityException(String message, String messageKey) {
        super(message);
        this.messageKey = messageKey;
    }

    public UnknownEntityException(String message, String messageKey, boolean internalServerException) {
        super(message);
        this.messageKey = messageKey;
        this.internalServerException = internalServerException;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public boolean isInternalServerException() {
        return internalServerException;
    }

    public boolean isUserLevelException() {
        return !internalServerException;
    }

}
