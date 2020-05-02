package com.weedow.spring.data.search.converter;

public class LocalDateConverter{}/* extends AbstractConverter {

    private static final int MAX_LOCAL_DATE_LENGTH = 10;

    @Override
    protected <T> T convertToType(Class<T> type, Object value) throws Throwable {
        String valueAsString = value.toString();
        if (valueAsString.length() > MAX_LOCAL_DATE_LENGTH) {
            valueAsString = valueAsString.substring(0, MAX_LOCAL_DATE_LENGTH);
        }
        return type.cast(LocalDate.parse(valueAsString));
    }

    @Override
    protected Class<?> getDefaultType() {
        return LocalDate.class;
    }
}
*/