package com.weedow.spring.data.search.sample.java.repository;

import com.weedow.spring.data.search.sample.java.entity.Address;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "addressDto", types = Address.class)
public interface AddressDto {

    String getStreet();

    String getCity();

    String getZipCode();

    String getCountry();

}
