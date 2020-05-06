package com.leyou.auth.client;

import com.leyou.user.api.UserAPI;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "user-service")
public interface UserClient extends UserAPI {
}
