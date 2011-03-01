package com.wadsack.android.sheepshead.scorer;

/**
 * Author: Jeremy Wadsack
 */
public class InvalidHandException extends Throwable {

    public InvalidHandException() {
        super("The Hand provided is not connected to this scorecard");
    }

    public InvalidHandException(String message) {
        super(message);
    }
}
