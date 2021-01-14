package com.weedow.searchy.sample.java;

import com.querydsl.core.JoinType;
import com.weedow.searchy.join.JoinInfo;
import com.weedow.searchy.join.handler.EntityJoinHandler;
import com.weedow.searchy.query.querytype.PropertyInfos;

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