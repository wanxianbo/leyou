package com.leyou.gateway.config;

import com.leyou.auth.utils.RsaUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.security.PublicKey;

@Data
@ConfigurationProperties(prefix = "ly.jwt")
public class JwtProperties {
    private String pubKeyPath; //公钥地址

    private PublicKey publicKey; //公钥

    private String cookieName; //cookie名称

    /**
     * @ PostConstruct：在构造方法执行之后执行该方法
     */
    @PostConstruct
    public void init() throws Exception {
        //获取公钥
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
    }
}
