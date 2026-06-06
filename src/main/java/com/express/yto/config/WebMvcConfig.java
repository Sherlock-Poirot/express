package com.express.yto.config;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private static final DateTimeFormatter[] FORMATTERS = {
            DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd"),
            DateTimeFormatter.ofPattern("yyyyMMdd")
    };

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToLocalDateConverter());
    }

    private static class StringToLocalDateConverter implements Converter<String, LocalDate> {
        @Override
        public LocalDate convert(String source) {
            if (source == null || source.trim().isEmpty()) {
                return null;
            }
            String trimmedSource = source.trim();
            
            if (trimmedSource.length() == 7 && trimmedSource.matches("\\d{4}-\\d{2}")) {
                return LocalDate.parse(trimmedSource + "-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }
            
            for (DateTimeFormatter formatter : FORMATTERS) {
                try {
                    return LocalDate.parse(trimmedSource, formatter);
                } catch (DateTimeParseException ignored) {
                }
            }
            throw new IllegalArgumentException("无法解析日期: " + source + "，支持的格式: yyyy-MM-dd, yyyy/MM/dd, yyyyMMdd, yyyy-MM");
        }
    }
}