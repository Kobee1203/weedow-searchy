package com.weedow.spring.data.search.sample.java;

import com.weedow.spring.data.search.config.SearchConfigurer;
import com.weedow.spring.data.search.descriptor.SearchDescriptor;
import com.weedow.spring.data.search.descriptor.SearchDescriptorBuilder;
import com.weedow.spring.data.search.descriptor.SearchDescriptorRegistry;
import com.weedow.spring.data.search.sample.java.entity.Person;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
@DependsOn("jpaSpecificationExecutorFactory")
public class SampleAppJavaConfiguration implements SearchConfigurer {

    @Override
    public void addSearchDescriptors(SearchDescriptorRegistry registry) {
        registry.addSearchDescriptor(personSearchDescriptor());
    }

    private SearchDescriptor<Person> personSearchDescriptor() {
        return new SearchDescriptorBuilder<>(Person.class).build();
    }
}
