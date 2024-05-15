package com.ulla.modules.assets.mo;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ulla.mybatis.BaseEntity;

import lombok.*;

/**
 * @Description {用户资金}
 * @author {clj}
 * @since {2023-2-16}
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_balance")
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BalanceMo extends BaseEntity {

    /**
     * 类型 0。奖励金 1。真实资金 2。机器人资金 3、虚拟资金
     */
    @TableField("type")
    private Integer type;

    /**
     * 剩余资金数额
     */
    @TableField("balance")
    private BigDecimal balance;

    /**
     * 冻结资金数额
     */
    @TableField("blocked_balance")
    private BigDecimal blockedBalance;

    /**
     * 用户Id
     */
    @TableField("user_id")
    private Long userId;

    /**
     * eo积分
     */
    @TableField("eo")
    private Integer eo;

}
