package com.weedow.searchy.sample.java.repository;

import com.weedow.searchy.sample.java.entity.Person;
import com.weedow.searchy.sample.java.entity.Vehicle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Projection(name = "personDto", types = Person.class)
public interface PersonDto {
    String getFirstName();

    String getLastName();

    @Value("#{target.firstName + ' ' + target.lastName}")
    String getFullName();

    String getEmail();

    Set<String> getNickNames();

    Set<String> getPhoneNumbers();

    @Value("#{T(com.weedow.searchy.sample.java.repository.PersonDto).formatAsString(target.getVehicles())}")
    List<String> getVehicles();

    @Value("#{target.addressEntities}")
    Set<AddressDto> getAddresses();

    static List<String> formatAsString(Set<Vehicle> vehicles) {
        return vehicles
                .stream()
                .map(vehicle -> vehicle.getVehicleType() + " - " + vehicle.getBrand() + "/" + vehicle.getModel())
                .collect(Collectors.toList());
    }

}
