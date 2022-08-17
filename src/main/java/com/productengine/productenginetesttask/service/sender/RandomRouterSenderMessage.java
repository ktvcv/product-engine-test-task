package com.productengine.productenginetesttask.service.sender;

import com.productengine.productenginetesttask.model.RouterMessageDTO;
import com.productengine.productenginetesttask.model.enums.MessageStatus;
import com.productengine.productenginetesttask.service.sender.feign.RouterMessageSenderClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.joining;

@Service
@ConditionalOnProperty(value = "router.message.mock.service", havingValue = "false", matchIfMissing = true)
public class RandomRouterSenderMessage implements RouterSenderMessage {

    public static final int BOUND = 256;
    private final Clock clock;
    private final RouterMessageSenderClient routerMessageSenderClient;
    private final Integer maxRouterMessageDelay;

    public RandomRouterSenderMessage(
        final Clock clock, final RouterMessageSenderClient routerMessageSenderClient,
        @Value("${router.message.max-possible-delay.in-seconds}") final Integer maxRouterMessageDelay
    ) {
        this.clock = clock;
        this.routerMessageSenderClient = routerMessageSenderClient;
        this.maxRouterMessageDelay = maxRouterMessageDelay;
    }

    @Scheduled(
        fixedRateString = "${router.message.interval.in-millis}"
    )
    public void sendMessage() {
        final ThreadLocalRandom localRandom = ThreadLocalRandom.current();
        final int randomNumber = localRandom.nextInt(BOUND);
        final String ipAddress = Collections.nCopies(4, randomNumber)
            .stream().map(String::valueOf)
            .collect(joining("."));

        final RouterMessageDTO routerMessage = new RouterMessageDTO()
            .setIp(ipAddress)
            .setStatus(MessageStatus.getRandom())
            .setCreatedAt(LocalDateTime.now(clock));

        sendMessage(routerMessage);
    }

    private void sendMessage(final RouterMessageDTO routerMessage) {
        final ThreadLocalRandom localRandom = ThreadLocalRandom.current();
        final int randomNumber = localRandom.nextInt(maxRouterMessageDelay);
        try {
            TimeUnit.SECONDS.sleep(randomNumber);
            routerMessageSenderClient.sendRouterMessage(routerMessage);
        } catch (final InterruptedException ignored) {
        }
    }
}
