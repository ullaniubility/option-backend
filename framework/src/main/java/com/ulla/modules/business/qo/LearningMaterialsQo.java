package com.ulla.modules.business.qo;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LearningMaterialsQo implements Serializable {

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

    @ApiModelProperty("视频url")
    @TableField("video_url")
    private String videoUrl;

    @ApiModelProperty("视频时长")
    @TableField("video_time")
    private Float videoTime;

    @ApiModelProperty("视频大小")
    @TableField("video_size")
    private Float videoSize;

    @ApiModelProperty("视频展示面路径")
    @TableField("video_picture")
    private String videoPicture;

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

}
