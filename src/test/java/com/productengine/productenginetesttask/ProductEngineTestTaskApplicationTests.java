package com.productengine.productenginetesttask;

import com.productengine.productenginetesttask.model.enums.RouterProblem;
import com.productengine.productenginetesttask.service.RouterProblemSaverService;
import com.productengine.productenginetesttask.service.sender.RouterSenderMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@SpringBootTest
@TestPropertySource(properties = "router.message.mock.service:true")
class ProductEngineTestTaskApplicationTests {

    @Autowired
    private RouterSenderMessage routerSenderMessage;

    @MockBean
    private RouterProblemSaverService saverService;

    @Test
    void checkLostAndFlappedRouterProblems() {
        routerSenderMessage.sendMessage();

        verify(saverService, times(1))
            .saveProblemRouters("2.2.2.2", RouterProblem.FLAPPED);
    }
}
