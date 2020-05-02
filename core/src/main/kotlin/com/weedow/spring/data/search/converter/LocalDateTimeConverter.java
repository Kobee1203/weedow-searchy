package com.weedow.spring.data.search.converter;

public class LocalDateTimeConverter{} /* extends AbstractConverter {

    @Override
    protected <T> T convertToType(Class<T> type, Object value) throws Throwable {
        String paramValueString = value.toString();
        try {
            return type.cast(LocalDateTime.parse(paramValueString));
        } catch (DateTimeParseException e) {
            // We drop the timezone info from the String:
            return type.cast(ZonedDateTime.parse(paramValueString).toLocalDateTime());
        }
    }

    @Override
    protected Class<?> getDefaultType() {
        return LocalDateTime.class;
    }
}
*/