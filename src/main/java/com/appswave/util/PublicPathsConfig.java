package com.appswave.util;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@PropertySource("classpath:application.properties")
@Data
public class PublicPathsConfig {
    @Value("${permitAllPaths}")
    private String permitAllPathsString;

    public List<String> getPermitAllPaths() {
        return Arrays.asList(permitAllPathsString.split(","));
    }

}
