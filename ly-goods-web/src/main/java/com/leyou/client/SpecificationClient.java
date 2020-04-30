package com.leyou.client;

import com.leyou.item.api.SpecificationAPI;
import org.springframework.cloud.openfeign.FeignClient;

//spring-cloud 在2.1.x以后@FeignClient中name不能一样,spring: main: allow-bean-definition-overriding: true
@FeignClient(name = "item-service")
public interface SpecificationClient extends SpecificationAPI {
}
