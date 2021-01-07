package com.weedow.spring.data.search.sample.java;

import com.querydsl.core.JoinType;
import com.weedow.spring.data.search.join.JoinInfo;
import com.weedow.spring.data.search.join.handler.EntityJoinHandler;
import com.weedow.spring.data.search.query.querytype.PropertyInfos;

import javax.persistence.ElementCollection;

/**
 * Fetch all fields annotated with @ElementCollection
 */
public class MyEntityJoinHandler implements EntityJoinHandler {

    @Override
    public boolean supports(PropertyInfos propertyInfos) {
        return propertyInfos.getAnnotations().stream().anyMatch(annotation -> annotation instanceof ElementCollection);
    }

    @Override
    public JoinInfo handle(PropertyInfos propertyInfos) {
        return new JoinInfo(JoinType.LEFTJOIN, true);
    }
}