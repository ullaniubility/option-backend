package com.ulla.modules.auth.qo;

import java.io.Serializable;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ulla.common.vo.PageVo;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @Description {用户后台qo}
 * @author {clj}
 * @since {2023-2-8}
 */
@ApiModel(value = "用户Qo")
@Data
@ToString
public class UserAdminQo extends PageVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户OPENID")
    private String openId;

    @ApiModelProperty(value = "uid")
    private Long uid;

    @ApiModelProperty(value = "用户邮箱")
    private String mail;

    @ApiModelProperty(value = "用户手机号")
    private String phone;

    @ApiModelProperty(value = "国家")
    private String area;

    @ApiModelProperty(value = "用户注册开始时间")
    private Long beginTime;

    @ApiModelProperty(value = "用户注册结束时间")
    private Long endTime;

    @ApiModelProperty(value = "身份类型 0.demo用户 ， 1.正式用户 , 2. 机器人")
    private Integer type;

    @ApiModelProperty(value = "删除标志 0.未删除 1. 删除")
    private Integer deleteFlag;

    @ApiModelProperty(value = "用户资金类型 0、奖励金额 1、真实资金  3、虚拟资金")
    private Integer balanceType;

    @ApiModelProperty(value = "用户资金类型起始资金")
    private BigDecimal beginBalance;

    @ApiModelProperty(value = "用户资金类型结束资金")
    private BigDecimal endBalance;

    public <T> QueryWrapper<T> queryWrapper() {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        if (CharSequenceUtil.isNotEmpty(openId)) {
            queryWrapper.like("a.open_id", openId);
        }
        if (ObjectUtil.isNotEmpty(uid)) {
            queryWrapper.like("a.uid", uid);
        }
        if (CharSequenceUtil.isNotEmpty(mail)) {
            queryWrapper.like("a.mail", mail);
        }
        if (CharSequenceUtil.isNotEmpty(phone)) {
            queryWrapper.like("a.phone", phone);
        }
        if (CharSequenceUtil.isNotEmpty(area)) {
            queryWrapper.like("a.area", area);
        }
        if (beginTime != null) {
            queryWrapper.ge("a.create_time", beginTime);
        }
        if (endTime != null) {
            queryWrapper.le("a.create_time", endTime);
        }
        if (type != null) {
            queryWrapper.eq("a.type", type);
        }
        if (balanceType != null) {
            switch (balanceType) {
                case 0: {
                    if (beginBalance != null) {
                        queryWrapper.ge("b.bonusBalance", beginBalance);
                    }
                    if (endBalance != null) {
                        queryWrapper.le("b.bonusBalance", endBalance);
                    }
                    break;
                }
                case 1: {
                    if (beginBalance != null) {
                        queryWrapper.ge("b.realBalance", beginBalance);
                    }
                    if (endBalance != null) {
                        queryWrapper.le("b.realBalance", endBalance);
                    }
                    break;
                }
                case 3: {
                    if (beginBalance != null) {
                        queryWrapper.ge("b.virtuallyBalance", beginBalance);
                    }
                    if (endBalance != null) {
                        queryWrapper.le("b.virtuallyBalance", endBalance);
                    }
                    break;
                }
            }
        }
        if (deleteFlag != null) {
            queryWrapper.eq("a.delete_flag", deleteFlag);
        }
        return queryWrapper;
    }

}
