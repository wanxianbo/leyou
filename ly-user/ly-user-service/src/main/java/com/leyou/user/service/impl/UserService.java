package com.leyou.user.service.impl;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.NumberUtils;
import com.leyou.user.config.CodeProperties;
import com.leyou.user.mapper.UserMapper;
import com.leyou.user.pojo.User;
import com.leyou.user.service.IUserService;
import com.leyou.user.utils.CodecUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@EnableConfigurationProperties(CodeProperties.class)
@Service
public class UserService implements IUserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private CodeProperties prop;
    //redis中code的key
    private static final String KEY_PREFIX = "user:code:phone:";

    /**
     * 注册时检验数据
     * @param data
     * @param type
     * @return
     */
    @Override
    public Boolean checkData(String data, Integer type) {
        User user = new User();
        //判断数据类型
        switch (type) {
            case 1:
                user.setUsername(data);
                break;
            case 2:
                user.setPhone(data);
                break;
            default:
                throw new LyException(ExceptionEnum.INVALID_USER_DATA_TYPE);
        }
        //查询数据库
        int count = userMapper.selectCount(user);
        return count == 0;
    }

    /**
     * 发送验证码
     * @param phone
     */
    @Override
    public void sendVerifyCode(String phone) {
        //生成验证码
        String key = KEY_PREFIX + phone;
        String code = NumberUtils.generateCode(6);
        //发送消息
        Map<String, String> msg = new HashMap<>();
        msg.put("phone", phone);
        msg.put("code", code);
        rabbitTemplate.convertAndSend("ly.sms.exchange","sms.verify.code",msg);
        //将code存入redis
        redisTemplate.opsForValue().set(key,code,prop.getCodeTimeout(), TimeUnit.MINUTES);
    }

    /**
     * 注册用户
     * @param user
     * @param code
     * @return
     */
    @Transactional
    @Override
    public Boolean register(User user, String code) {
        //从redis中取出验证码
        String key = KEY_PREFIX + user.getPhone();
        String codeCache = redisTemplate.opsForValue().get(key);
        //校验验证码
        if (!code.equals(codeCache)) {
            return false;
        }
        user.setId(null);
        user.setCreated(new Date());

        //生成盐
        String salt = CodecUtils.generateSalt();
        user.setSalt(salt);
        //对数据加密
        user.setPassword(CodecUtils.md5Hex(user.getPassword(),salt));
        //存入数据库
        Boolean boo =userMapper.insertSelective(user) == 1;
        //如果成功，则删除redis中的code
        if (boo) {
            try {
                redisTemplate.delete(key);
            } catch (Exception e) {
               log.error("[用户服务] 删除缓存验证码失败，code:{}",code);
            }
        }
        return boo;
    }

    /**
     * 查询用户
     * @param username
     * @param password
     * @return
     */
    @Override
    public User query(String username, String password) {
        //创建user
        User record = new User();
        record.setUsername(username);
        //查询数据库
        User user = userMapper.selectOne(record);
        //检验用户名和密码
        if (user == null || !user.getPassword().equals(CodecUtils.md5Hex(password, user.getSalt()))) {
            throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
        }
        log.info("[用户服务] 查询成功 用户名:{}",username);
        return user;
    }
}
