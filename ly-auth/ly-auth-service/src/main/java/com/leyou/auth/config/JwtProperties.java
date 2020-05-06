package com.leyou.auth.config;

import com.leyou.auth.utils.RsaUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;

@Data
@ConfigurationProperties(prefix = "ly.jwt")
public class JwtProperties {
    private String secret; //登录校验的密钥
    private String pubKeyPath; //公钥地址
    private String priKeyPath; //私钥地址
    private int expire; //过期时间,单位分钟

    private PublicKey publicKey; //公钥
    private PrivateKey privateKey; //私钥

    private String cookieName; //cookie名称
    private int cookieMaxAge; //cookie的生存时间

    /**
     *  @ PostContruct：在构造方法执行之后执行该方法
     */
    @PostConstruct
    public void init() throws Exception {
        File pubKey = new File(pubKeyPath);
        File priKey = new File(priKeyPath);
        if (!pubKey.exists() || !priKey.exists()) {
            //生成公钥和私钥
            RsaUtils.generateKey(pubKeyPath, priKeyPath, secret);
        }
        //获取公钥和私钥
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }
}
