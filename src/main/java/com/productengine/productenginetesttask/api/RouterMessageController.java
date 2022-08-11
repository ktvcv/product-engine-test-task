package com.productengine.productenginetesttask.api;

import com.productengine.productenginetesttask.model.RouterMessageDTO;
import com.productengine.productenginetesttask.service.RouterMessageService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public final class RouterMessageController {

    private final RouterMessageService routerMessageService;

    public RouterMessageController(final RouterMessageService routerMessageService) {
        this.routerMessageService = routerMessageService;
    }

    @PostMapping("/api/v1/router-message")
    public void saveRouterMassage(@RequestBody final RouterMessageDTO routerMessageDTO){
        routerMessageService.saveRouterMessage(routerMessageDTO);
    }
}
