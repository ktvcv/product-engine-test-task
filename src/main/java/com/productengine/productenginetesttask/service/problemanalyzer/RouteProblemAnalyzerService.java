package com.productengine.productenginetesttask.service.problemanalyzer;

import com.productengine.productenginetesttask.model.RouterMessageDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;

@Service
public class RouteProblemAnalyzerService {

    private final List<RouterProblemAnalyzer> problemAnalyzers;

    public RouteProblemAnalyzerService(final List<RouterProblemAnalyzer> problemAnalyzers) {
        this.problemAnalyzers = problemAnalyzers;
    }

    public void analyzeUpcomingMessage(
        final Map<String, Set<RouterMessageDTO>> messages, final LocalDateTime now
    ) {
        new ForkJoinPool(problemAnalyzers.size())
            .submit(() ->
                problemAnalyzers
                    .parallelStream()
                    .forEach(routerProblemAnalyzer -> routerProblemAnalyzer.saveProblemIfFound(messages, now))
            ).join();

    }
}
