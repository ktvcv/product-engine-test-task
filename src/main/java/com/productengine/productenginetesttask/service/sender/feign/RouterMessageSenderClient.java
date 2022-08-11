package com.productengine.productenginetesttask.service.sender.feign;

import com.productengine.productenginetesttask.model.RouterMessageDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "router-message-client", url = "${router.message.api.url}")
public interface RouterMessageSenderClient {

    @PostMapping(value = "/api/v1/router-message")
    void sendRouterMessage(
        @RequestBody final RouterMessageDTO routerMessage
    );
}
