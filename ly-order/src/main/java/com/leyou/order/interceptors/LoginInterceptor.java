package com.leyou.order.interceptors;

import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.utils.CookieUtils;
import com.leyou.order.config.JwtProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    private JwtProperties prop;

    //定义一个线程域，存放user信息
    private static final ThreadLocal<UserInfo> tl = new ThreadLocal<>();

    public LoginInterceptor(JwtProperties prop) {
        this.prop = prop;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        //获取cookie
        String token = CookieUtils.getCookieValue(request, prop.getCookieName());
        //解析token
        try {

            UserInfo user = JwtUtils.getInfoFromToken(token, prop.getPublicKey());
            tl.set(user);
            return true;
        } catch (Exception e) {
            log.error("[购物车服务] 获取用户身份信息失败 ",e);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        //最后移除掉tl的值
        tl.remove();
    }

    /**
     * 定于一个对外获取user的方法
     * @return
     */
    public static UserInfo getUser() {
        return tl.get();
    }
}
