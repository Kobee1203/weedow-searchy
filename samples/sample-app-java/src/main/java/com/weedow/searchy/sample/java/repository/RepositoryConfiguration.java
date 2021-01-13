package com.weedow.searchy.sample.java.repository;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;

@Configuration
public class RepositoryConfiguration implements RepositoryRestConfigurer {

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        config.getProjectionConfiguration().addProjection(PersonDto.class);
        config.getProjectionConfiguration().addProjection(AddressDto.class);
        config.getProjectionConfiguration().addProjection(VehicleDto.class);
    }
}