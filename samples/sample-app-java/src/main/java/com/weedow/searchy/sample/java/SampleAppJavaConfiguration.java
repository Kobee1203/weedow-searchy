package com.weedow.searchy.sample.java;

import com.weedow.searchy.config.SearchyConfigurer;
import com.weedow.searchy.descriptor.SearchyDescriptor;
import com.weedow.searchy.descriptor.SearchyDescriptorBuilder;
import com.weedow.searchy.descriptor.SearchyDescriptorRegistry;
import com.weedow.searchy.sample.java.entity.Person;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SampleAppJavaConfiguration implements SearchyConfigurer {

    @Override
    public void addSearchyDescriptors(SearchyDescriptorRegistry registry) {
        registry.addSearchyDescriptor(personSearchyDescriptor());
    }

    private SearchyDescriptor<Person> personSearchyDescriptor() {
        return new SearchyDescriptorBuilder<>(Person.class).build();
    }
}
