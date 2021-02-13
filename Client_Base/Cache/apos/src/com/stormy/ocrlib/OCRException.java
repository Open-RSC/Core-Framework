package com.stormy.ocrlib;

public class OCRException extends Exception {

    private static final long serialVersionUID = -3043287754156453842L;

    public OCRException() {
        super();
    }

    public OCRException(String message) {
        super(message);
    }

    public OCRException(String message, Throwable cause) {
        super(message, cause);
    }

    public OCRException(Throwable cause) {
        super(cause);
    }
}
