package com.elec5619p5g1.finance_tracker.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("${file.upload.path:/var/www/uploads/}")
    String UPLOAD_ROOT_PATH;

    @Value("${file.visit.path:/resources/}")
    String VISIT_PATH;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(VISIT_PATH + "**")
                .addResourceLocations("file:" + UPLOAD_ROOT_PATH);
    }
}
