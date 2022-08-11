package com.productengine.productenginetesttask.model.enums;

public enum MessageStatus {

    GONE(false), AVAILABLE(true);

    private final boolean isSuccessStatus;

    MessageStatus(final boolean isSuccessStatus) {
        this.isSuccessStatus = isSuccessStatus;
    }

    public boolean isSuccessStatus() {
        return isSuccessStatus;
    }

    public static MessageStatus getRandom(final int randomNumber) {
        if (randomNumber % 3 == 0){
            return GONE;
        }
        return AVAILABLE;
    }
}
