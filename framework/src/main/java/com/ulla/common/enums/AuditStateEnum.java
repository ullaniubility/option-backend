package com.ulla.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuditStateEnum {

    // 列类型 0、其他 1、整数型 2、浮点数型 3、字符串 4、布尔型
    OTHER(0, "禁用"),

    INTEGER(1, "启用"),

    FLOAT(2, "待审核");

    private final Integer columnType;

    private final String desc;

    /**
     * 自己定义一个静态方法,通过value返回枚举常量对象
     */
    public static AuditStateEnum of(int columnType) {
        for (AuditStateEnum action : values()) {
            if (action.getColumnType() == columnType) {
                return action;
            }
        }
        return AuditStateEnum.OTHER;
    }
}
