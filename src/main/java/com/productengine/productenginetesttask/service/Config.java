package com.productengine.productenginetesttask.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class Config {

    private final Long fixedRate;

    public Config(@Value("${router.message.interval.in-seconds}") final Long fixedRate) {
        this.fixedRate = fixedRate;
    }

    @Bean
    public Long checkInterval(){
        return fixedRate;
    }

    @Bean
    public Long doubleInterval(){
        return fixedRate * 2;
    }

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
