package com.weedow.searchy.sample.java.repository;

import com.weedow.searchy.sample.java.entity.Address;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "addressDto", types = Address.class)
public interface AddressDto {

    String getStreet();

    String getCity();

    String getZipCode();

    String getCountry();

}
