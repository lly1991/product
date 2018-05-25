package com.lly.product.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * @author lly
 * @Description TODO
 * @date 2018/5/25上午9:39
 * @since JDK1.8
 */
@Component
@ConfigurationProperties("girl")
@RefreshScope
@Data
public class GirlConfig
{
    private String name;
    private Integer age;
}
