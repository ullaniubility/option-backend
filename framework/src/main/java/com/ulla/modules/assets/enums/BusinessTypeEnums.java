package com.ulla.modules.assets.enums;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 1
 */
@AllArgsConstructor
@Getter
public enum BusinessTypeEnums {
    /**
     * 其他
     */
    OTHER(0, new ArrayList<>(), "", "其他"),
    /**
     * 入金，金额仅为正数
     */
    DEPOSIT(1, BalanceTypeEnums.getUserBalanceByDeposit(), "DEP", "入金"),
    /**
     * 提现，金额仅为负数
     */
    WITHDRAW(2, BalanceTypeEnums.getUserBalanceByWithdraw(), "WIT", "提现"),
    /**
     * 交易，金额不允许为0
     */
    EXCHANGE(3, BalanceTypeEnums.getUserBalanceByExchange(), "EXC", "交易"),
    /**
     * 系统，金额不允许为0
     */
    SYSTEM(4, BalanceTypeEnums.getUserBalanceBySystem(), "SYS", "系统奖励"),
    /**
     * @Description 管理员操作
     * @author zhuyongdong
     * @since 2023-04-11 16:07:11
     */
    ADMIN(5, BalanceTypeEnums.getUserBalanceByExchange(), "ADMIN", "管理员操作"),

    /**
     * @Description 平仓
     * @author zhuyongdong
     * @since 2023-05-10 19:31:52
     */
    CLOSE_ORDER(6, BalanceTypeEnums.getUserBalanceBySystem(), "CLOSE", "平仓"),

    /**
     * @Description eo积分兑换
     * @author chen
     * @since 2023-05-10 19:31:52
     */
    POINT_EXCHANGE(7, BalanceTypeEnums.getUserBalanceBySystem(), "POINT", "积分兑换");

    /**
     * 业务类型
     */
    private final Integer type;
    /**
     * 可操作的账户余额类型
     */
    private final List<BalanceTypeEnums> operableBalanceType;
    /**
     * 流水号前缀
     */
    private final String prefix;
    /**
     * 描述
     */
    private final String desc;

    public static BusinessTypeEnums of(int columnType) {
        for (BusinessTypeEnums action : values()) {
            if (action.getType() == columnType) {
                return action;
            }
        }
        return BusinessTypeEnums.OTHER;
    }

}
