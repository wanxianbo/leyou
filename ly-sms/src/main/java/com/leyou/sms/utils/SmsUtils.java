package com.leyou.sms.utils;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.leyou.sms.config.SmsProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@EnableConfigurationProperties(SmsProperties.class)
public class SmsUtils {

    @Autowired
    private SmsProperties prop;

    @Autowired
    private StringRedisTemplate redisTemplate;
    //初始化ascClient需要的几个参数
    static final String product = "Dysmsapi";//短信API产品名称（短信产品名固定，无需修改）
    static final String domain = "dysmsapi.aliyuncs.com";//短信API产品域名（接口地址固定，无需修改）
    //定义redis的key的值
    private static final String KEY_PERFIX = "sms:phone:";

    public SendSmsResponse sendsSms(String phoneNumber, String signName, String templateCode, String templateParam) {
        //给redis的key赋值
        String key = KEY_PERFIX + phoneNumber;
        //设置超时时间-可自行调整
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");
        //替换成你的AK
        final String accessKeyId = "yourAccessKeyId";//你的accessKeyId,参考本文档步骤2
        final String accessKeySecret = "yourAccessKeySecret";//你的accessKeySecret，参考本文档步骤2
        String lastTime = redisTemplate.opsForValue().get(key);
        if (StringUtils.isNotBlank(lastTime)) {
            Long last = Long.valueOf(lastTime);
            if (System.currentTimeMillis() - last < 60000) {
                //如果小于60秒，就不发短信
                log.info("[短信服务] 短信发送频率过高被拦截 手机号码:{}",phoneNumber);
                return null;
            }
        }

        //初始化ascClient,暂时不支持多region（请勿修改）
        try {
            IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", prop.getAccessKeyId(),
                    prop.getAccessKeySecret());
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
            IAcsClient acsClient = new DefaultAcsClient(profile);
            //组装请求对象
            SendSmsRequest request = new SendSmsRequest();
            //使用post提交
            request.setMethod(MethodType.POST);
            //必填:待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式；发送国际/港澳台消息时，接收号码格式为国际区号+号码，如“85200000000”
            request.setPhoneNumbers(phoneNumber);
            //必填:短信签名-可在短信控制台中找到
            request.setSignName(signName);
            //必填:短信模板-可在短信控制台中找到，发送国际/港澳台消息时，请使用国际/港澳台短信模版
            request.setTemplateCode(templateCode);
            //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
            //友情提示:如果JSON中需要带换行符,请参照标准的JSON协议对换行符的要求,比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败
            request.setTemplateParam(templateParam);
            //请求失败这里会抛ClientException异常
            SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
            if (sendSmsResponse.getCode() == null || !"OK".equals(sendSmsResponse.getCode())) {
                //请求失败
                log.info("[短信服务] 发送短信失败 手机号码:{},原因:{}", phoneNumber, sendSmsResponse.getMessage());
            }
            //向redis存入短信的时间
            redisTemplate.opsForValue().set(key, String.valueOf(System.currentTimeMillis()),1, TimeUnit.MINUTES);
            log.info("[短信服务] 发送短信成功 手机号码:{}", phoneNumber);
            return sendSmsResponse;
        } catch (ClientException e) {
            log.error("[短信服务] 发送短信异常 手机号码:{}", phoneNumber, e);
            return null;
        }
    }
}
