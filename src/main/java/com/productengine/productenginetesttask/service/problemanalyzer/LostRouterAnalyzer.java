package com.productengine.productenginetesttask.service.problemanalyzer;

import com.productengine.productenginetesttask.model.RouterMessageDTO;
import com.productengine.productenginetesttask.model.enums.RouterProblem;
import com.productengine.productenginetesttask.service.RouterProblemSaverService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.productengine.productenginetesttask.model.enums.RouterProblem.LOST;
import static java.util.stream.Collectors.partitioningBy;

@Component
public class LostRouterAnalyzer implements RouterProblemAnalyzer {

    private final Integer lostIntervalInSeconds;
    private final Long messageInterval;
    private final RouterProblemSaverService routerProblemSaverService;

    public LostRouterAnalyzer(
        @Value("${router.message.interval.in-seconds}") final Integer lostIntervalInSeconds,
        final RouterProblemSaverService routerProblemSaverService,
        final Long doubleInterval
    ) {
        this.lostIntervalInSeconds = lostIntervalInSeconds;
        this.routerProblemSaverService = routerProblemSaverService;
        this.messageInterval = doubleInterval;
    }

    @Override
    public void saveProblemIfFound(
        final Map<String, Set<RouterMessageDTO>> messagesByRouter, final LocalDateTime now
    ) {
        messagesByRouter
            .entrySet()
            .parallelStream()
            .filter(routerMessage -> isFailed(routerMessage.getValue(), now))
            .forEach(entry -> routerProblemSaverService.saveProblemRouters(entry.getKey(), getProblemType()));
    }

    private boolean isFailed(
        final Set<RouterMessageDTO> messages,
        final LocalDateTime now
    ) {
        final Map<Boolean, List<RouterMessageDTO>> messagesByAvailable =
            getMessagesByAvailable(messages, now);
        final List<RouterMessageDTO> goneMessages = messagesByAvailable.get(false);
        if (!goneMessages.isEmpty()) {
            final List<RouterMessageDTO> availableMessages = messagesByAvailable.get(true);
            for (RouterMessageDTO message : goneMessages) {
                final LocalDateTime maxDelayMessageTime = message.getCreatedAt().plusSeconds(lostIntervalInSeconds);
                if (availableMessages.isEmpty()){
                    return true;

                }
                if (availableMessages
                    .stream()
                    .noneMatch(sum -> sum.getCreatedAt().isBefore(maxDelayMessageTime))) {
                    return true;
                }
            }
        }
        return false;
    }

    private Map<Boolean, List<RouterMessageDTO>> getMessagesByAvailable(
        final Set<RouterMessageDTO> messages, final LocalDateTime now
    ) {
        return messages
            .stream()
            .filter(message -> isNotProcessedMessages(message, now))
            .collect(partitioningBy(RouterMessageDTO::isAvailable));
    }

    private boolean isNotProcessedMessages(
        final RouterMessageDTO messageDTO,
        final LocalDateTime now
    ) {
        return messageDTO.getCreatedAt().isAfter(now.minusSeconds(messageInterval));
    }

    @Override
    public RouterProblem getProblemType() {
        return LOST;
    }
}
