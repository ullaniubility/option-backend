package com.ulla.modules.auth.vo;

import java.io.Serializable;

import com.ulla.common.utils.StringUtils;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description 用户等级视图
 * @author zhuyongdong
 * @since 2023-05-16 20:12:04
 */
@Data
public class UserLevelTranVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "说明")
    private String description;

    @ApiModelProperty(value = "模拟用户")
    private String demo;

    @ApiModelProperty(value = "等级Micro")
    private String micro;

    @ApiModelProperty(value = "等级Basic")
    private String basic;

    @ApiModelProperty(value = "等级Silver")
    private String silver;

    @ApiModelProperty(value = "等级Gold")
    private String gold;

    @ApiModelProperty(value = "等级Platinum")
    private String platinum;

    @ApiModelProperty(value = "等级Exclusive")
    private String exclusive;

    public String getPlatinumByTran() {
        if (StringUtils.isNotBlank(platinum) && platinum.equals("99999999")) {
            return "无限制";
        }
        return platinum;
    }

    public String getExclusiveByTran() {
        if (StringUtils.isNotBlank(exclusive)) {
            if (exclusive.equals("99999999")) {
                return "无限制";
            } else if ("null".equals(exclusive)) {
                return "请联系账户经理获取更多信息";
            }
            return exclusive;
        }
        return "请联系账户经理获取更多信息";
    }

}
