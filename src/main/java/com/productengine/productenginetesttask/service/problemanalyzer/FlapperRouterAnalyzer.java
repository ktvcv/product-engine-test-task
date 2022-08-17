package com.productengine.productenginetesttask.service.problemanalyzer;

import com.productengine.productenginetesttask.model.RouterMessageDTO;
import com.productengine.productenginetesttask.model.enums.RouterProblem;
import com.productengine.productenginetesttask.service.RouterProblemSaverService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;

import static com.productengine.productenginetesttask.model.enums.RouterProblem.FLAPPED;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Component
public class FlapperRouterAnalyzer implements RouterProblemAnalyzer {

    private final Integer flapperMessagesCount;
    private final Integer flappedIntervalInSeconds;
    private final Long messageInterval;
    private final RouterProblemSaverService routerProblemSaverService;


    public FlapperRouterAnalyzer(
        @Value("${router.message.flap.times}") final Integer flapperMessagesPairs,
        @Value("${router.message.interval.in-seconds}") final Integer flappedIntervalInSeconds,
        final Long doubleInterval,
        final RouterProblemSaverService routerProblemSaverService
    ) {
        this.flapperMessagesCount = flapperMessagesPairs * 2;
        this.flappedIntervalInSeconds = flappedIntervalInSeconds;
        this.messageInterval = doubleInterval;
        this.routerProblemSaverService = routerProblemSaverService;
    }

    @Override
    public void saveProblemIfFound(
        final Map<String, Set<RouterMessageDTO>> messages, final LocalDateTime now
    ) {
        messages
            .entrySet()
            .parallelStream()
            .filter(entry -> checkPair(entry.getValue(), now))
            .forEach(entry -> routerProblemSaverService.saveProblemRouters(entry.getKey(), getProblemType()));
    }

    private boolean checkPair(final Set<RouterMessageDTO> messages, final LocalDateTime now) {
        final List<RouterMessageDTO> goneMessages = messages
            .stream()
            .filter(message -> isNotProcessedMessages(message, now) && !message.isAvailable())
            .collect(toList());

        return goneMessages
            .parallelStream()
            .map(goneMessage -> checkForFlappedMessages(goneMessage, messages))
            .anyMatch(flappedMessages -> flappedMessages.equals(flapperMessagesCount));
    }

    private int checkForFlappedMessages(
        final RouterMessageDTO goneMessage, final Set<RouterMessageDTO> messages
    ) {
        final LocalDateTime maxDelayMessageTime = goneMessage.getCreatedAt().plusSeconds(flappedIntervalInSeconds);
        final TreeMap<LocalDateTime, RouterMessageDTO> map = messages
            .stream()
            .collect(
                toMap(RouterMessageDTO::getCreatedAt, Function.identity(), (o1, o2) -> o1, TreeMap::new));

        return getFlappedMessages(goneMessage, 1, map, maxDelayMessageTime);

    }

    private int getFlappedMessages(
        final RouterMessageDTO goneMessage, int flapperTimes,
        final TreeMap<LocalDateTime, RouterMessageDTO> map, final LocalDateTime maxDelayMessageTime
    ) {
        final Optional<RouterMessageDTO> next = getNext(goneMessage, map, maxDelayMessageTime);
        if (next.isPresent() && ++flapperTimes <= flapperMessagesCount - 1) {
            return getFlappedMessages(next.get(), flapperTimes, map, maxDelayMessageTime);
        }
        return flapperTimes;

    }

    private Optional<RouterMessageDTO> getNext(
        final RouterMessageDTO previous,
        final TreeMap<LocalDateTime, RouterMessageDTO> map, final LocalDateTime maxDelayMessageTime
    ) {
        final Optional<Map.Entry<LocalDateTime, RouterMessageDTO>> next =
            Optional.ofNullable(map.higherEntry(previous.getCreatedAt()));
        if (next.isPresent()) {
            final RouterMessageDTO nextMessage = next.get().getValue();
            if (nextMessage.getCreatedAt().isBefore(maxDelayMessageTime)
                && previous.hasOppositeStatus(nextMessage.getStatus())
            ) {
                return Optional.of(nextMessage);
            }
        }
        return Optional.empty();
    }

    private boolean isNotProcessedMessages(
        final RouterMessageDTO messageDTO,
        final LocalDateTime now
    ) {
        return messageDTO.getCreatedAt().isAfter(now.minusSeconds(messageInterval));
    }

    @Override
    public RouterProblem getProblemType() {
        return FLAPPED;
    }

}
