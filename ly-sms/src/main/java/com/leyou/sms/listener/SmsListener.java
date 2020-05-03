package com.leyou.sms.listener;

import com.leyou.sms.config.SmsProperties;
import com.leyou.sms.utils.SmsUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Map;

@Component
@EnableConfigurationProperties(SmsProperties.class)
public class SmsListener {

    @Autowired
    private SmsUtils smsUtils;
    @Autowired
    private SmsProperties prop;

    public void listenSendMessage(Map<String,String> msg) {
        if (CollectionUtils.isEmpty(msg)) {
            //放弃处理
            return;
        }
        String phone = msg.get("phone");
        String code = msg.get("code");
        if (StringUtils.isBlank(phone) || StringUtils.isBlank(code)) {
            //放弃发送
            return;
        }
        //处理消息，调用发送短信的工具类
        smsUtils.sendsSms(phone, prop.getSignName(), prop.getVerifyCodeTemplate(), code);
    }
}
