package com.leyou.auth.test;

import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.auth.utils.RsaUtils;
import org.junit.Before;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;

public class JwtTest {
    private static final String pubKeyPath = "E:\\my_java\\uploads\\rsa\\rsa.pub";

    private static final String priKeyPath = "E:\\my_java\\uploads\\rsa\\rsa.pri";

    private PublicKey publicKey;

    private PrivateKey privateKey;

    /**
     * 生成公钥和私钥
     *
     * @throws Exception
     */
    @Test
    public void testRsa() throws Exception {
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "234");
    }

    /**
     * 在生成密钥时，注释Before，在生成token时，去掉注释Before
     * @throws Exception
     */
    @Before
    public void testGetRsa() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }

    /**
     * 生成token
     *
     * @throws Exception
     */
    @Test
    public void testGenerateToken() throws Exception {
        // 生成token
        String token = JwtUtils.generateToken(new UserInfo(20L, "jack"), privateKey, 5);
        System.out.println("token = " + token);
    }

    /**
     * 解析token
     *
     * @throws Exception
     */
    @Test
    public void testParseToken() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MjAsInVzZXJuYW1lIjoiamFjayIsImV4cCI6MTU4ODY2OTc0OH0.Y6So4_Hg98Nly9f5NZ8xmnQk6r2DgP_ejuhr6xn432zSr_V3Kmm7J79KUapjyUWjkoTLH9bQOOKH6DDmo788odifT3Jj2h-zfCytkdbOolgVJHTstlzNU4MK3yRQOD9Fn_wFi1oeDIH9LNufKCM81mxlU-Lp9Agfxy8iTv4P_ia6sd8xGwN1lEgvTB1uUP_L0QlfPX-ImOfGmPTwvXeEn3sNettip_YuBlA5NSl0sRSwxBm-6v1QsexrsykjfbP9p87l-2ySbJmQ3AEwWLiZIA6o-8ghfkP1BoFcBl9IU1-z1md_c51YO8a6OgnhZugMzkuw55Yvox7Wcfyjbk_DFg";

        // 解析token
        UserInfo user = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println("id: " + user.getId());
        System.out.println("userName: " + user.getUsername());
    }
}
