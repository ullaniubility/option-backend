package com.ulla.modules.assets.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 1
 */

@AllArgsConstructor
@Getter
public enum BalanceTypeEnums {
    BONUS_BALANCE(0, "奖励"), REAL_BALANCE(1, "充值"), ROBOT_BALANCE(2, "机器人余额"), VIRTUAL_BALANCE(3, "虚拟余额");

    private final Integer type;
    private final String desc;

    public static List<Integer> getUserType() {
        return Arrays.stream(new BalanceTypeEnums[] {REAL_BALANCE, BONUS_BALANCE, VIRTUAL_BALANCE})
            .map(BalanceTypeEnums::getType).collect(Collectors.toList());
    }

    public static BalanceTypeEnums getDescByType(Integer type) {
        return Arrays.stream(values()).filter(balanceTypeEnum -> balanceTypeEnum.type.intValue() == type.intValue())
            .collect(Collectors.toList()).get(0);
    }

    public static List<BalanceTypeEnums> getUserBalanceByDeposit() {
        return Arrays.asList(REAL_BALANCE, BONUS_BALANCE);
    }

    public static List<BalanceTypeEnums> getUserBalanceByWithdraw() {
        return Collections.singletonList(REAL_BALANCE);
    }

    public static List<BalanceTypeEnums> getUserBalanceByExchange() {
        return Arrays.asList(BONUS_BALANCE, REAL_BALANCE, VIRTUAL_BALANCE);
    }

    public static List<BalanceTypeEnums> getUserBalanceBySystem() {
        return Arrays.asList(ROBOT_BALANCE);
    }
}
