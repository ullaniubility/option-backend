package com.ulla.modules.assets.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import com.ulla.modules.assets.enums.BusinessTypeEnums;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description {用户资金}
 * @author {clj}
 * @since {2023-2-16}
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BalanceChangeVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 变动的真实余额
     */
    private BigDecimal amount;

    /**
     * 变动的奖金余额
     */
    private BigDecimal bonusAmount;

    /**
     * 变动的虚拟余额
     */
    private BigDecimal virtualAmount;

    /**
     * 用户Id
     */
    private Long uid;

    /**
     * 用户uId(openId)
     */
    private String openId;

    /**
     * 业务类型枚举
     */
    private BusinessTypeEnums businessTypeEnums;

    /**
     * 业务订单号
     */
    private String businessNo;

    /**
     * @Description 说明
     * @author zhuyongdong
     * @since 2023-04-11 17:07:19
     */
    private String remark;

}
