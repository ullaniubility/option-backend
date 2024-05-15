package com.ulla.modules.payment.vo;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 系统支付渠道(SysChannel)表实体类
 *
 * @author hgw
 * @since 2022-12-01 15:00:11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "sys_channel-系统支付渠道")
public class SysChannel implements Serializable {
    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "渠道名称")
    private String channelName;

    @ApiModelProperty(value = "渠道标识-渠道唯一的标识")
    private String channelIdent;

    @ApiModelProperty(value = "支付接口类型-支付类型， 银行卡，信用卡等")
    private String payType;

    @ApiModelProperty(value = "渠道ICON")
    private String channelIcon;

    @ApiModelProperty(value = "URL网址")
    private String url;

    @ApiModelProperty(value = "接口是否异常 0没异常 1异常")
    private Integer errorState;

    @ApiModelProperty(value = "渠道状态-是否启用 0启动 1未启动")
    private Integer channelState;

    @ApiModelProperty(value = "创建人id")
    private Long createdById;

    @ApiModelProperty(value = "创建人")
    private String createdBy;

    @ApiModelProperty(value = "创建时间")
    private Date createdTime;

    @ApiModelProperty(value = "更新人id")
    private Long updatedById;

    @ApiModelProperty(value = "更新人")
    private String updatedBy;

    @ApiModelProperty(value = "更新时间")
    private Date updatedTime;

    @ApiModelProperty(value = "渠道属性")
    private String chAttribute;

    @ApiModelProperty(value = "0加密货币类型  1国际法币")
    private Integer channelType;

    //最小值
    @TableField(exist = false)
    private String buyEachTimeMin;
    //最大值
    @TableField(exist = false)
    private String buyEachTimeMax;

}

