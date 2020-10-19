package com.weedow.spring.data.search.sample.java;

import com.weedow.spring.data.search.sample.java.entity.Person;
import com.weedow.spring.data.search.join.JoinInfo;

import javax.persistence.ElementCollection;
import javax.persistence.criteria.JoinType;
import java.lang.annotation.Annotation;

public class MyEntityJoinHandler implements com.weedow.spring.data.search.join.handler.EntityJoinHandler<Person> {

    @Override
    public boolean supports(Class<?> entityClass, Class<?> fieldClass, String fieldName, Annotation joinAnnotation) {
        return joinAnnotation instanceof ElementCollection;
    }

    @Override
    public JoinInfo handle(Class<?> entityClass, Class<?> fieldClass, String fieldName, Annotation joinAnnotation) {
        return new JoinInfo(JoinType.LEFT, true);
    }
}