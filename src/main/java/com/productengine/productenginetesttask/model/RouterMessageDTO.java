package com.productengine.productenginetesttask.model;

import com.productengine.productenginetesttask.model.enums.MessageStatus;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public final class RouterMessageDTO implements Comparable<RouterMessageDTO>{

    private String ip;
    private MessageStatus status;
    private LocalDateTime timeStamp;

    @Override
    public int compareTo(final RouterMessageDTO routerMessageDTO) {
        return this.timeStamp.compareTo(routerMessageDTO.timeStamp);
    }
}
