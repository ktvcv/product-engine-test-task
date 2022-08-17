package com.productengine.productenginetesttask.service;

import com.productengine.productenginetesttask.model.enums.RouterProblem;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
@Log
public class RouterProblemSaverService {

    public void saveProblemRouters(final String routerIpAddress, final RouterProblem problem) {
        log.info(format("A problem %s occurred with router %s", problem, routerIpAddress));
    }
}
