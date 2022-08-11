package com.productengine.productenginetesttask.service;

import com.productengine.productenginetesttask.model.RouterMessageDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public final class RouterMessageService {

    private final ConcurrentHashMap<String, Set<RouterMessageDTO>> upcomingMessages;
    private final String maxRouterMessageDelay;

    public RouterMessageService(
        @Value("${router.message.max-possible-delay.in-seconds}") final String maxRouterMessageDelay
    ) {
        this.maxRouterMessageDelay = maxRouterMessageDelay;
        this.upcomingMessages = new ConcurrentHashMap<>();
    }

    public void saveRouterMessage(final RouterMessageDTO routerMessage) {
        upcomingMessages
            .computeIfAbsent(
                routerMessage.getIp(),
                ip -> new TreeSet<>())
            .add(routerMessage);
    }

    @Scheduled(
        fixedRateString = "${router.message.rate-time.in-seconds}",
        initialDelayString = "${router.message.rate-time.in-seconds}",
        timeUnit = TimeUnit.SECONDS
    )
    private void cleanRoutersMessages() {
        upcomingMessages.clear();
    }

}
