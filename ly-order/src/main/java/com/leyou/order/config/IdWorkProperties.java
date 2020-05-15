package com.leyou.order.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "ly.work")
public class IdWorkProperties {
    private long workerId;
    private long datacenterId;
}
