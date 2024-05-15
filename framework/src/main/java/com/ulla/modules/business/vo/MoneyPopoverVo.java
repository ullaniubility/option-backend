package com.ulla.modules.business.vo;

import java.io.Serializable;
import lombok.Data;

@Data
public class MoneyPopoverVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 新用户弹窗：1开启，0关闭
     */
    private Integer isDisable = 0;

    /**
     * 时间
     */
    private String hours = "1";

    /**
     * 金额
     */
    private String moneys = "100，300，500";

    /**
     * 文本
     */
    private String content = "入金成功";

    /**
     * 文本
     */
    private Integer times = 2;

    /**
     * 受欢迎程度
     */
    private String isPopular = "100";

    /**
     * 促销活动id
     */
    private String activeId;
}
