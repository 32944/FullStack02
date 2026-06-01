package com.itheima.graduation.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "jwt")//读取配置文件中的jwt相关的配置
public class JwtConfig {
    private String secret;
    private Long expiration;
}
