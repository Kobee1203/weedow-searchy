package com.weedow.spring.data.search.converter;

public class DefaultStringConverter {}/* extends AbstractConverter {

    @Override
    @SuppressWarnings("unchecked")
    protected <T> T convertToType(Class<T> type, Object value) throws Throwable {
        if (Enum.class.isAssignableFrom(type)) {
            final Class<? extends Enum> enumType = (Class<? extends Enum>) type;
            return (T) Enum.valueOf(enumType, value.toString());
        }
        return type.cast(value.toString());
    }

    @Override
    protected Class<?> getDefaultType() {
        return String.class;
    }
}
*/