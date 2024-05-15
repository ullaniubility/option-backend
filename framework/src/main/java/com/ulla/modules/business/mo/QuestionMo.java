package com.ulla.modules.business.mo;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@TableName("sys_question")
@ApiModel(value = "Question对象", description = "")
public class QuestionMo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("标题")
    @TableField("question")
    private String question;

    @ApiModelProperty("内容")
    @TableField("answer")
    private String answer;

    @ApiModelProperty("创建时间")
    @TableField("create_time")
    private Long createTime;

    @ApiModelProperty("更新时间")
    @TableField("update_time")
    private Long updateTime;

    @ApiModelProperty("编辑人uid")
    @TableField("operator_uid")
    private Long operatorUid;

    @ApiModelProperty("排序:1置顶0正常")
    @TableField("sort")
    private Integer sort;

    @ApiModelProperty("0禁用1启用")
    @TableField("`status_type`")
    private Integer statusType;

}
