package com.weedow.spring.data.search.sample.java;

import com.weedow.spring.data.search.sample.java.entity.Person;
import com.weedow.spring.data.search.join.JoinInfo;
import org.jetbrains.annotations.NotNull;

import javax.persistence.ElementCollection;
import javax.persistence.criteria.JoinType;
import java.lang.annotation.Annotation;

public class MyEntityJoinHandler implements com.weedow.spring.data.search.join.handler.EntityJoinHandler<Person> {

    @Override
    public boolean supports(@NotNull Class<?> entityClass, @NotNull Class<?> fieldClass, @NotNull String fieldName, @NotNull Annotation joinAnnotation) {
        return joinAnnotation instanceof ElementCollection;
    }

    @Override
    public JoinInfo handle(@NotNull Class<?> entityClass, @NotNull Class<?> fieldClass, @NotNull String fieldName, @NotNull Annotation joinAnnotation) {
        return new JoinInfo(JoinType.LEFT, true);
    }
}