package com.lostpetfinder.exception;

/** Thrown when a business rule is violated (e.g. matching a resolved report). */
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) { super(message); }
}
