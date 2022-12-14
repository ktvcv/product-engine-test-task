package com.productengine.productenginetesttask.service.sender;

import com.productengine.productenginetesttask.model.MockRouterMessage;
import com.productengine.productenginetesttask.model.RouterMessageDTO;
import com.productengine.productenginetesttask.service.RouterMessageService;
import lombok.SneakyThrows;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.productengine.productenginetesttask.model.enums.MessageStatus.AVAILABLE;
import static com.productengine.productenginetesttask.model.enums.MessageStatus.GONE;

@Service
@ConditionalOnProperty(value = "router.message.mock.service", havingValue = "true")
public class MockRouterSenderMessage implements RouterSenderMessage {

    private final RouterMessageService routerMessageService;

    private final static List<MockRouterMessage> lostRouterMessages = new ArrayList<>();
    private final static List<MockRouterMessage> flappedRouterMessage = new ArrayList<>();

    public static final LocalDateTime START = LocalDateTime.now(Clock.systemUTC());

    public static final String SECOND_IP = "2.2.2.2";
    public static final String FIRST_IP = "1.1.1.1";

    static {
        //lost problem
        lostRouterMessages.add(
            new MockRouterMessage()
                .setDelayInSeconds(4)
                .setRouterMessageDTO(new RouterMessageDTO()
                    .setIp(FIRST_IP)
                    .setCreatedAt(START.plusSeconds(3))
                    .setStatus(GONE)));
        // received with delay, ignored
        lostRouterMessages.add(
            new MockRouterMessage()
                .setDelayInSeconds(40)
                .setRouterMessageDTO(new RouterMessageDTO()
                    .setIp(FIRST_IP)
                    .setCreatedAt(START.plusSeconds(5))
                    .setStatus(AVAILABLE)));
        lostRouterMessages.add(
            new MockRouterMessage()
                .setDelayInSeconds(80)
                .setRouterMessageDTO(new RouterMessageDTO()
                    .setIp(FIRST_IP)
                    .setCreatedAt(START.plusSeconds(70))
                    .setStatus(AVAILABLE)));


        //flapped
        flappedRouterMessage.add(
            new MockRouterMessage()
                .setDelayInSeconds(7)
                .setRouterMessageDTO(new RouterMessageDTO()
                    .setIp(SECOND_IP)
                    .setCreatedAt(START.plusSeconds(5))
                    .setStatus(GONE)));
        flappedRouterMessage.add(
            new MockRouterMessage()
                .setDelayInSeconds(13)
                .setRouterMessageDTO(new RouterMessageDTO()
                    .setIp(SECOND_IP)
                    .setCreatedAt(START.plusSeconds(10))
                    .setStatus(AVAILABLE)));
        flappedRouterMessage.add(
            new MockRouterMessage()
                .setDelayInSeconds(17)
                .setRouterMessageDTO(new RouterMessageDTO()
                    .setIp(SECOND_IP)
                    .setCreatedAt(START.plusSeconds(15))
                    .setStatus(GONE)));
        flappedRouterMessage.add(
            new MockRouterMessage()
                .setDelayInSeconds(22)
                .setRouterMessageDTO(new RouterMessageDTO()
                    .setIp(SECOND_IP)
                    .setCreatedAt(START.plusSeconds(20))
                    .setStatus(AVAILABLE)));
        flappedRouterMessage.add(
            new MockRouterMessage()
                .setDelayInSeconds(27)
                .setRouterMessageDTO(new RouterMessageDTO()
                    .setIp(SECOND_IP)
                    .setCreatedAt(START.plusSeconds(25))
                    .setStatus(GONE)));
        flappedRouterMessage.add(
            new MockRouterMessage()
                .setDelayInSeconds(32)
                .setRouterMessageDTO(new RouterMessageDTO()
                    .setIp(SECOND_IP)
                    .setCreatedAt(START.plusSeconds(30))
                    .setStatus(AVAILABLE)));
    }

    public MockRouterSenderMessage(final RouterMessageService routerMessageService) {
        this.routerMessageService = routerMessageService;
    }

    @SneakyThrows
    @Override
    public void sendMessage() {
        sendMessages();

        //to wait for messages analyze
        TimeUnit.SECONDS.sleep(120);
    }

    private void sendMessages() {
        lostRouterMessages.addAll(flappedRouterMessage);
        lostRouterMessages.forEach(mockMessage -> new Thread(() ->
            {
                try {
                    TimeUnit.SECONDS.sleep(mockMessage.getDelayInSeconds());
                    routerMessageService.saveRouterMessage(mockMessage.getRouterMessageDTO());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start()
        );
    }

}