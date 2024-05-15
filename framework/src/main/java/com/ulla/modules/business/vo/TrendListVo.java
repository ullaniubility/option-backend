package com.ulla.modules.business.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhuyongdong
 * @Description 趋势渲染数据
 * @since 2023/2/28 18:22
 */
@Data
public class TrendListVo {

    /**
     * 二级交易类名称
     */
    @ApiModelProperty("二级交易类名称")
    private String childName;

    /**
     * 涨平跌 1.涨 0.平 -1.跌
     */
    @ApiModelProperty("涨平跌 1.涨 0.平 -1.跌")
    private Integer upAndDownSign;

    /**
     * 涨平跌元素数量
     */
    @ApiModelProperty("涨平跌元素数量")
    private Integer upAndDownSignNumber;

}
