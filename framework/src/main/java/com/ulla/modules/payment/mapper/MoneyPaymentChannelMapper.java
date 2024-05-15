package com.ulla.modules.payment.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ulla.modules.payment.entity.MoneyPaymentChannelEntity;

/**
 * @Description 公司收款链配置
 * @author zhuyongdong
 * @since 2023-05-06 18:05:29
 */
@Mapper
public interface MoneyPaymentChannelMapper extends BaseMapper<MoneyPaymentChannelEntity> {

}
