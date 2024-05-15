package com.ulla.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.ulla.common.utils.HttpUtil;
import com.ulla.common.utils.UserUtil;

/**
 * @author zhuyongdong
 * @Description TODO
 * @since 2023/4/19 10:47
 */
@Component
public class BaseInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        UserUtil.getCurrent().set(HttpUtil.getCommonHeader(request));
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
        ModelAndView modelAndView) {}

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
        Exception ex) {
        UserUtil.getCurrent().remove();
    }
}
