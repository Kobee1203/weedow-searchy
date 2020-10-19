package com.weedow.spring.data.search.sample.java;

import com.weedow.spring.data.search.sample.java.entity.Person;
import com.weedow.spring.data.search.config.SearchConfigurer;
import com.weedow.spring.data.search.descriptor.SearchDescriptor;
import com.weedow.spring.data.search.descriptor.SearchDescriptorBuilder;
import com.weedow.spring.data.search.descriptor.SearchDescriptorRegistry;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SampleAppJavaConfiguration implements SearchConfigurer {

    @Override
    public void addSearchDescriptors(SearchDescriptorRegistry registry) {
        registry.addSearchDescriptor(personSearchDescriptor());
    }

    private SearchDescriptor<Person> personSearchDescriptor() {
        return new SearchDescriptorBuilder<>(Person.class).build();
    }
}
