package com.ulla.common.controller;

import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.ulla.common.utils.TokenUtils;
import com.ulla.modules.auth.mo.LoginUserMo;

/**
 * @author zhuyongdong
 * @Description 基础的类
 * @since 2023/3/30 20:39
 */
public class BaseController {

    private static final Logger logger = LoggerFactory.getLogger(BaseController.class);

    @Autowired
    protected HttpServletRequest request;

    public LoginUserMo getLoginUser() {
        try {
            if (Objects.isNull(request)) {
                request = ((ServletRequestAttributes)(RequestContextHolder.currentRequestAttributes())).getRequest();
            }
            LoginUserMo loginUserMo = TokenUtils.getLoginUser(request);
            if (loginUserMo == null) {
                return null;
            }
            return loginUserMo;
        } catch (Exception e) {
            logger.error("=====>getLoginUser()出错", e.getMessage(), e);
            return null;
        }
    }
}
