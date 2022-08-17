package com.productengine.productenginetesttask.model;

import com.productengine.productenginetesttask.model.enums.MessageStatus;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class RouterMessageDTO implements Comparable<RouterMessageDTO>{

    private String ip;
    private MessageStatus status;
    private LocalDateTime createdAt;

    private LocalDateTime receivedAt;

    @Override
    public int compareTo(final RouterMessageDTO routerMessageDTO) {
        return this.receivedAt.compareTo(routerMessageDTO.getReceivedAt());
    }

    public boolean isAvailable(){
        return status.isSuccessStatus();
    }

    public  boolean isOpposite(final MessageStatus messageStatus){
        return this.status != messageStatus;
    }

}
