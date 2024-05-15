package com.ulla.modules.business.qo;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PrivacyQo implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;

    @ApiModelProperty("当前页")
    @TableField(exist = false)
    private Integer page = 1;

    @ApiModelProperty("每页大小")
    @TableField(exist = false)
    private Integer limit = 20;

    @ApiModelProperty("查询区间")
    @TableField(exist = false)
    private Long beginTime;
    @ApiModelProperty("查询区间")
    @TableField(exist = false)
    private Long endTime;

    @ApiModelProperty("标题")
    @TableField("title")
    private String title;

    @ApiModelProperty("内容")
    @TableField("content")
    private String content;

    @ApiModelProperty("创建时间")
    @TableField("create_time")
    private Long createTime;

    @ApiModelProperty("更新时间")
    @TableField("update_time")
    private Long updateTime;

    @ApiModelProperty("编辑人id")
    @TableField("operator_uid")
    private Long operatorUid;

    @ApiModelProperty("排序:1置顶0正常")
    @TableField("sort")
    private Integer sort;

    @ApiModelProperty("0禁用1启用")
    @TableField("`status_type`")
    private Integer statusType;

    @ApiModelProperty("0是用户注册时的隐私政策，1是用户入金时的支付政策")
    @TableField("`form`")
    private Integer form;

}
