package com.sandeep.yabonza.exception;

public class YabonzaException extends RuntimeException {

    public YabonzaException(String errorMsg, Exception e) {
        super(errorMsg, e);
    }

    public YabonzaException(String errorMsg) {
        super(errorMsg);
    }
}
