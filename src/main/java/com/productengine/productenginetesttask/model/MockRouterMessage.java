package com.productengine.productenginetesttask.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MockRouterMessage  {

    private int delayInSeconds;
    private RouterMessageDTO routerMessageDTO;
}
