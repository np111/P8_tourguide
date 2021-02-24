package com.tourguide.gps.config;

import com.tourguide.gps.util.StringToLocationConverter;
import com.tourguide.spring.NoContentFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToLocationConverter());
    }

    @Bean
    public NoContentFilter getNoContentFilter() {
        return new NoContentFilter();
    }
}
