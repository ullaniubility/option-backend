package com.ulla.common.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 常用的公共方法
 */
@Service
public class PaymentUtil {


    /**
     * 计算偏差 ： （旧的汇率 减去 新的汇率）除以 旧的汇率 = 偏差滑点
     * 判断偏差滑点是否大于1  isPoint 为true 偏差滑点在百分之 10以内 为false 偏差滑点在百分之10以外
     * @param oldExchangeRate 旧的汇率 - 估算汇率 - 交易按钮点击时上一次的汇率
     * @param exchangeRate 现有汇率 - 精准汇率 - 交易按钮点击时计算的汇率
     * @return
     */
    public Boolean isSlipPoint(BigDecimal oldExchangeRate ,BigDecimal exchangeRate){
        Boolean isPoint = false;
        //计算偏差 ： （旧的汇率 减去 新的汇率）除以 旧的汇率 = 偏差滑点
        BigDecimal difference  =(oldExchangeRate.subtract(exchangeRate)).divide(oldExchangeRate,BigDecimal.ROUND_FLOOR);
        BigDecimal min = new BigDecimal(-0.1);
        BigDecimal max = new BigDecimal(0.1);
        //判断偏差滑点是否大于1  slippage 为1 偏差滑点在百分之一以内 为0 偏差滑点在百分之一以外
        if(difference.compareTo(min)>=0 && difference.compareTo(max) <= 0){
            isPoint = true;
        }else{
            isPoint = false;
        }
        return isPoint;
    }


}
