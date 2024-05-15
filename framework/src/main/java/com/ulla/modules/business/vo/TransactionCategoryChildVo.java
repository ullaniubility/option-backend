package com.ulla.modules.business.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhuyongdong
 * @Description TODO
 * @since 2023/2/28 18:22
 */
@Data
public class TransactionCategoryChildVo {

    /**
     * 数据编号
     */
    @ApiModelProperty("数据编号")
    private Long id;

    /**
     * 二级交易类名称
     */
    @ApiModelProperty("二级交易类名称")
    private String childName;
    /**
     * 二级交易类图标链接
     */
    @ApiModelProperty("二级交易类图标链接")
    private String logoUrl;

    /**
     * 盈利百分比（%）
     */
    @ApiModelProperty("盈利百分比（%）")
    private Integer profitPercent;

    /**
     * 一级交易类编号
     */
    @ApiModelProperty("一级交易类编号")
    private Long categoryId;

    /**
     * 一级交易类名称
     */
    @ApiModelProperty("一级交易类名称")
    private String categoryName;

    /**
     * 是否启用 0.启用 1.关闭
     */
    @ApiModelProperty("是否启用 0.启用 1.关闭")
    private Integer usingFlag;

    /**
     * 涨平跌 1.涨 0.平 -1.跌
     */
    @ApiModelProperty("涨平跌 1.涨 0.平 -1.跌")
    private Integer upAndDownSign;

    /**
     * 涨跌百分比(%)
     */
    @ApiModelProperty("涨跌百分比(%)")
    private String upAndDownPercent;

    /**
     * 结算时间档位编号
     */
    @ApiModelProperty("结算时间档位编号")
    private Long sectionId;

    /**
     * 是否活跃：1活跃 0不活跃
     */
    @ApiModelProperty("是否活跃：1活跃 0不活跃")
    private Integer isPopular;

}
