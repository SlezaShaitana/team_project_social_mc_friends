package com.social.mc_friends.service.impl;

import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.ServiceInstanceListSuppliers;
import org.springframework.context.annotation.Bean;

public class TestClientLoadBalancer {
    private static final String SERVICE_ID = "session-service";

    @Bean
    public ServiceInstanceListSupplier staticServiceInstanceListSupplier() {
        return ServiceInstanceListSuppliers.from(SERVICE_ID,
                new DefaultServiceInstance(SERVICE_ID + "-1", SERVICE_ID, "localhost", 8888, false));
    }
}
