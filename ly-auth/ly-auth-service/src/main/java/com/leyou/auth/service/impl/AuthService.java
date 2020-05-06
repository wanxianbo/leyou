package com.leyou.auth.service.impl;

import com.leyou.auth.client.UserClient;
import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.service.IAuthService;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.user.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

@Slf4j
@EnableConfigurationProperties(JwtProperties.class)
@Service
public class AuthService implements IAuthService {

    @Autowired
    private UserClient userClient;
    @Autowired
    private JwtProperties prop;

    /**
     * 登入账号，并使用jwt进行加密
     * @param username
     * @param password
     * @return
     */
    @Override
    public String authentication(String username, String password) {
        //根据用户名和密码校验
        User user = userClient.queryUser(username, password);
        if (user == null) {
            throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
        }
        try {
            //生成token
            String token = JwtUtils.generateToken(new UserInfo(user.getId(), username), prop.getPrivateKey(), prop.getExpire());
            return token;
        } catch (Exception e) {
            log.error("[授权中心] 用户名或密码有误 用户名称:{}",username,e);
            throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
        }
    }
}
