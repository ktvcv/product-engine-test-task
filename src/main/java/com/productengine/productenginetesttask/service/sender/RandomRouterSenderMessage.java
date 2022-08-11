package com.productengine.productenginetesttask.service.sender;

import com.productengine.productenginetesttask.model.RouterMessageDTO;
import com.productengine.productenginetesttask.model.enums.MessageStatus;
import com.productengine.productenginetesttask.service.sender.feign.RouterMessageSenderClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.joining;

@Service
public final class RandomRouterSenderMessage {

    public static final int BOUND = 3;
    private final RouterMessageSenderClient routerMessageSenderClient;

    public RandomRouterSenderMessage(final RouterMessageSenderClient routerMessageSenderClient) {
        this.routerMessageSenderClient = routerMessageSenderClient;
    }

    @Scheduled(
        fixedRateString = "${router.message.interval.in-seconds}",
        initialDelay = 1,
        timeUnit = TimeUnit.SECONDS
    )
    public void cleanRoutersMessages() {
        final ThreadLocalRandom localRandom = ThreadLocalRandom.current();
        final int randomNumber = localRandom.nextInt(BOUND);
        final String s = Collections.nCopies(4, randomNumber)
            .stream().map(String::valueOf)
            .collect(joining("."));

        final RouterMessageDTO routerMessage = new RouterMessageDTO()
            .setIp(s)
            .setStatus(MessageStatus.getRandom(randomNumber))
            .setTimeStamp(LocalDateTime.now());
        sendMessage(routerMessage);

    }

    private void sendMessage(final RouterMessageDTO routerMessage) {
        final ThreadLocalRandom localRandom = ThreadLocalRandom.current();
        final int randomNumber = localRandom.nextInt(BOUND * 10);
        try {
            TimeUnit.SECONDS.sleep(randomNumber);
            routerMessageSenderClient.sendRouterMessage(routerMessage);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
    }
}
