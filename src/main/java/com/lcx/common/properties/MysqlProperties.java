package com.lcx.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "spring.datasource")
public class MysqlProperties {

    private String url;
    private String username;
    private String password;
    private String path;
    private String database;
    private int day;
}
