package com.productengine.productenginetesttask.service.problemanalyzer;

import com.productengine.productenginetesttask.model.RouterMessageDTO;
import com.productengine.productenginetesttask.model.enums.RouterProblem;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

public interface RouterProblemAnalyzer {

    void saveProblemIfFound(
        final Map<String, Set<RouterMessageDTO>> messages,
        final LocalDateTime now
    );

    RouterProblem getProblemType();
}
