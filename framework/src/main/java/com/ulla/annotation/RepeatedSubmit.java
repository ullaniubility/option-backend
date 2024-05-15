package com.ulla.annotation;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.ulla.cache.Cache;
import com.ulla.common.enums.ResultCodeEnums;
import com.ulla.common.vo.exception.ServiceException;

import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhuyongdong
 * @Description 防重复提交实现
 * @since 2023/4/22 11:58
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RepeatedSubmit {

    final Cache cache;

    @Before("@annotation(repeatedSubmitAspect)")
    public void interceptor(RepeatedSubmitAspect repeatedSubmitAspect) {

        try {
            String redisKey = getParams();
            Long count = cache.incr(redisKey, repeatedSubmitAspect.expire());
            log.debug("Anti repeated submission：params-{},value-{}", redisKey, count);
            // 如果超过0或者设置的参数，则表示重复提交了
            if (count.intValue() > 0) {
                throw new ServiceException(ResultCodeEnums.LIMIT_ERROR);
            }
        }
        // 如果参数为空，则表示用户未登录，直接略过，不做处理
        catch (NullPointerException e) {
            return;
        } catch (ServiceException e) {
            if (e instanceof ServiceException) {
                ServiceException serviceException = e;
                ResultCodeEnums resultCode = serviceException.getResultCodeEnums();
                String message = null;
                if (resultCode != null) {
                    message = resultCode.message();
                }
                // 如果有扩展消息，则输出异常中，跟随补充异常
                if (!serviceException.getMsg().equals(ServiceException.DEFAULT_MESSAGE)) {
                    message += ":" + serviceException.getMsg();
                }
                serviceException.setMsg(message);
                log.error("全局异常[ServiceException]:{}-{}", resultCode.code(), resultCode.message(), e);
                throw serviceException;
            } else {
                log.error("全局异常[ServiceException]:", e);
                throw e;
            }
        } catch (Exception e) {
            log.error("Exception of anti repeated submission interceptor", e);
            throw new ServiceException(ResultCodeEnums.ERROR);
        }
    }

    /**
     * 获取表单参数
     *
     * @return 计数器key
     */
    private String getParams() {
        HttpServletRequest request =
            ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
        StringBuilder stringBuilder = new StringBuilder();
        // 拼接请求地址
        stringBuilder.append(request.getRequestURI());

        // 参数不为空则拼接参数
        if (!request.getParameterMap().isEmpty()) {
            stringBuilder.append(JSONUtil.toJsonStr(request.getParameterMap()));
        }
        // 请求地址
        return stringBuilder.toString();
    }
}
