package com.leyou.search.client;

import com.leyou.item.api.CategoryAPI;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "item-service")
public interface CategoryClient extends CategoryAPI {
}
