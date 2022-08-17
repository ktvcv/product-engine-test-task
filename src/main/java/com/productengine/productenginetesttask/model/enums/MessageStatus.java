package com.productengine.productenginetesttask.model.enums;

import java.util.concurrent.ThreadLocalRandom;

public enum MessageStatus {

    GONE(false), AVAILABLE(true);

    private final boolean isSuccessStatus;

    MessageStatus(final boolean isSuccessStatus) {
        this.isSuccessStatus = isSuccessStatus;
    }

    public boolean isSuccessStatus() {
        return isSuccessStatus;
    }

    public static MessageStatus getRandom() {
        final ThreadLocalRandom random = ThreadLocalRandom.current();
        if (random.nextInt(100) % 3 == 0) {
            return GONE;
        }
        return AVAILABLE;
    }
}
