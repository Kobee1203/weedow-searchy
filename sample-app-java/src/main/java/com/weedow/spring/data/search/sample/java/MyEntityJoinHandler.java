package com.weedow.spring.data.search.sample.java;

import com.querydsl.core.JoinType;
import com.weedow.spring.data.search.join.JoinInfo;
import com.weedow.spring.data.search.join.handler.EntityJoinHandler;
import com.weedow.spring.data.search.querydsl.querytype.PropertyInfos;
import com.weedow.spring.data.search.sample.java.entity.Person;
import org.jetbrains.annotations.NotNull;

import javax.persistence.ElementCollection;

public class MyEntityJoinHandler implements EntityJoinHandler<Person> {

    @Override
    public boolean supports(@NotNull PropertyInfos propertyInfos) {
        return propertyInfos.getAnnotations().stream().anyMatch(annotation -> annotation instanceof ElementCollection);
    }

    @NotNull
    @Override
    public JoinInfo handle(@NotNull PropertyInfos propertyInfos) {
        return new JoinInfo(JoinType.LEFTJOIN, true);
    }
}