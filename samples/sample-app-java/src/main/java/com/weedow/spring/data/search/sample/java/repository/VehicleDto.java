package com.weedow.spring.data.search.sample.java.repository;

import com.weedow.spring.data.search.sample.java.entity.Vehicle;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "vehicleDto", types = Vehicle.class)
public interface VehicleDto {
}
