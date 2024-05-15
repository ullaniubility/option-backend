package com.ulla.modules.auth.service.impl;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.ulla.cache.CachePrefix;
import com.ulla.cache.impl.RedisCache;
import com.ulla.constant.SysConfigConstant;
import com.ulla.constant.VerificationConstant;
import com.ulla.modules.admin.mapper.SysConfigMapper;
import com.ulla.modules.auth.service.SmsAndMailService;

import cn.hutool.http.HttpRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description {用户qo}
 * @author {clj}
 * @since {2023-2-15}
 */
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SmsAndMailServiceImpl implements SmsAndMailService {

    final SysConfigMapper sysConfigMapper;

    final RedisCache redisCache;

    final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.properties.mail.display.sendname}")
    private String sendname;

    @Override
    public boolean sendSms(String phone, String phoneCode, VerificationConstant.Verify_Type type) {
        if (StringUtils.isNotEmpty(phoneCode.trim())
            && (phoneCode.trim().equals("+86") || phoneCode.trim().equals("86"))) {
            throw new RuntimeException("no support for Chinamainland Users！");
        }
        Random rand = new Random();
        int code = rand.nextInt(100000) + 100000;
        String mobilePhone = phone;
        if (StringUtils.isNotEmpty(phoneCode)) {
            phone = phoneCode + phone;
        }
        String key = sysConfigMapper.selectByKey(SysConfigConstant.SMS_KEY);
        String gateway = sysConfigMapper.selectByKey(SysConfigConstant.SMS_URL);
        String account = sysConfigMapper.selectByKey(SysConfigConstant.SMS_ACCOUNT);
        Map<String, String> params = new HashMap<>();
        params.put("account", account);
        params.put("password", key);
        String smsContent = "【WorldCrypt】Your verification code is:[" + code + "]";
        params.put("msg", smsContent);
        params.put("mobile", phone);
        log.info("节点全球发送短信====", params);

        String jsonString = JSON.toJSONString(params);
        System.out.println(jsonString);

        String body =
            HttpRequest.post(gateway).header("Content-Type", "application/json").body(jsonString).execute().body();
        System.out.println("------短信回调显示-----：" + body);
        HashMap<String, String> map = JSON.parseObject(body, (Type)Map.class);
        if (map.get("code").equals("108")) {
            return Boolean.FALSE;
        }
        if (map.get("code").equals("0")) {
            redisCache.setExpire(CachePrefix.SMS_CODE + mobilePhone + "-" + type, String.valueOf(code), 300);
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Override
    public boolean sendMail(String mailbox, VerificationConstant.Verify_Type type) {
        Random rand = new Random();
        int code = rand.nextInt(100000) + 100000;
        String msg = "【" + sendname + "】Your email verification code for this operation is:" + code
            + "   Valid for 5 minutes, please ignore if not operated by yourself！";
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(username);
            message.setTo(mailbox);
            message.setSubject(sendname);
            message.setText(msg);
            mailSender.send(message);
        } catch (MailException e) {
            log.error(e.getMessage());
        }
        redisCache.setExpire(CachePrefix.EMAIL_CODE + mailbox + "-" + type, String.valueOf(code), 300);
        // Map<String, Object> map = new HashMap<>();
        // map.put("account", mailbox);
        // map.put("msg", msg);
        // map.put("type", type);
        return Boolean.TRUE;
    }

}
