package com.ulla.common.vo.exception;

import static com.ulla.constant.NumberConstant.NULL_STRING;

import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ulla.common.utils.ResultUtil;
import com.ulla.common.vo.ResultMessageVo;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zhuyongdong
 * @Description 全局notnull异常捕获
 * @since 2023/2/28 0:55
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResultMessageVo handleException(HttpMessageNotReadableException e) {
        log.error("请求体为空", e);
        return ResultUtil.error(4002, "The request body cannot be empty");
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultMessageVo handleException(MethodArgumentNotValidException e) {
        String err = e.getBindingResult().getFieldError() != null
            ? e.getBindingResult().getFieldError().getDefaultMessage() : e.getMessage();
        log.error(err, e);
        return ResultUtil.error(4002, err);
    }

    @ResponseBody
    @ExceptionHandler(ServiceException.class)
    public ResultMessageVo handleException(ServiceException e) {
        log.error(e.getMsg());
        return ResultUtil.error(4002, e.getMsg());
    }

    /**
     * 参数校验异常统一处理 validation Exception
     */
    @ResponseBody
    @ExceptionHandler({ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultMessageVo handleConstraintViolationException(ConstraintViolationException exception) {
        boolean present = exception.getConstraintViolations().stream().findFirst().isPresent();
        String message = NULL_STRING;
        if (present) {
            message = exception.getConstraintViolations().stream().findFirst().get().getMessage();
        }
        log.error("参数异常,ex = {}", exception.getMessage());
        return ResultUtil.error(4002, message);
    }
}
