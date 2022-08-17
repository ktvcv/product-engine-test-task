package com.productengine.productenginetesttask.service;

import com.productengine.productenginetesttask.model.RouterMessageDTO;
import com.productengine.productenginetesttask.service.problemanalyzer.RouteProblemAnalyzerService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;

@Service
@Log
public final class RouterMessageService {

    private final RouteProblemAnalyzerService routeProblemAnalyzerService;
    private final ConcurrentHashMap<String, Set<RouterMessageDTO>> upcomingMessages;
    private final Clock clock;
    private final Integer maxRouterMessageDelay;

    public RouterMessageService(
        final RouteProblemAnalyzerService routeProblemAnalyzerService,
        final Clock clock, @Value("${router.message.max-possible-delay.in-seconds}") final Integer maxRouterMessageDelay
    ) {
        this.routeProblemAnalyzerService = routeProblemAnalyzerService;
        this.clock = clock;
        this.maxRouterMessageDelay = maxRouterMessageDelay;
        this.upcomingMessages = new ConcurrentHashMap<>();
    }

    public void saveRouterMessage(final RouterMessageDTO routerMessage) {
        routerMessage.setReceivedAt(LocalDateTime.now(clock));
        log.info(format("Received a router message %s", routerMessage));

        if (!isMessageDelayed(routerMessage)) {
            upcomingMessages.computeIfAbsent(routerMessage.getIp(), ip -> new HashSet<>()).add(routerMessage);
        }
    }

    private boolean isMessageDelayed(
        final RouterMessageDTO routerMessage
    ) {
        return ChronoUnit.SECONDS
            .between(routerMessage.getCreatedAt(), routerMessage.getReceivedAt()) > maxRouterMessageDelay;
    }

    @Scheduled(
        fixedRateString = "#{checkInterval}",
        initialDelayString = "#{checkInterval}",
        timeUnit = TimeUnit.SECONDS
    )
    private void findRoutersProblems() {
        log.info("Start problem analyzing");
        routeProblemAnalyzerService.analyzeUpcomingMessage(upcomingMessages, LocalDateTime.now(clock));
    }

}
