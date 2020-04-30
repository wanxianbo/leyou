package com.leyou.client;

import com.leyou.item.api.CategoryAPI;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "item-service")
public interface CategoryClient extends CategoryAPI {
}
