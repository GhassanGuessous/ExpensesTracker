package com.guess.expensestracker.web.rest.errors;

public class BadRequestAlertException extends RuntimeException {

    private String entityName;

    public BadRequestAlertException(String message, String entityName) {
        super(message);
        this.entityName = entityName;
    }

    public String getEntityName() {
        return entityName;
    }
}
