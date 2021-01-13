package com.weedow.searchy.sample.reactive.config;

import com.weedow.searchy.common.model.Person;
import com.weedow.searchy.config.SearchyConfigurer;
import com.weedow.searchy.descriptor.SearchyDescriptor;
import com.weedow.searchy.descriptor.SearchyDescriptorBuilder;
import com.weedow.searchy.descriptor.SearchyDescriptorRegistry;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SampleReactiveAppConfiguration implements SearchyConfigurer {

    @Override
    public void addSearchyDescriptors(SearchyDescriptorRegistry registry) {
        registry.addSearchyDescriptor(personSearchyDescriptor());
    }

    private SearchyDescriptor<Person> personSearchyDescriptor() {
        return new SearchyDescriptorBuilder<>(Person.class).build();
    }
}
