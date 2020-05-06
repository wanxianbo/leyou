package com.leyou.auth.web;

import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.service.IAuthService;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.CookieUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@EnableConfigurationProperties(JwtProperties.class)
@RestController
public class AuthController {
    @Autowired
    private IAuthService authService;
    @Autowired
    private JwtProperties prop;

    /**
     * 登录账号
     * @param username
     * @param password
     * @param request
     * @param response
     * @return
     */
    @PostMapping("/accredit")
    public ResponseEntity<Void> authentication(@RequestParam("username") String username,
                                               @RequestParam("password") String password,
                                               HttpServletRequest request, HttpServletResponse response) {
        //登录校验将,得到token
        String token = authService.authentication(username, password);
        //将token存入cookie中，返回到客服端
        CookieUtils.setCookie(request, response, prop.getCookieName(),
                token, prop.getCookieMaxAge(),null,true);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 校验是否登录
     * @param token
     * @param request
     * @param response
     * @return
     */
    @GetMapping("/verify")
    public ResponseEntity<UserInfo> verify(@CookieValue("LY_TOKEN") String token,
                                           HttpServletRequest request,HttpServletResponse response) {
        try {
            //解析token
            UserInfo user = JwtUtils.getInfoFromToken(token, prop.getPublicKey());
            //刷新token
            String newToken = JwtUtils.generateToken(user, prop.getPrivateKey(), prop.getExpire());
            CookieUtils.setCookie(request,response,prop.getCookieName(),newToken,
                    prop.getCookieMaxAge(),null,true);
            //已登录,返回用户信息
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            throw new LyException(ExceptionEnum.UNAUTHORIZED);
        }
    }
}
