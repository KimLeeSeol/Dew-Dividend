package com.kdew.Dew_Stock_Dividend.exception;

public abstract class AbstractException extends RuntimeException {

    abstract public int getStatusCode();
    abstract public String getMessage();
}
