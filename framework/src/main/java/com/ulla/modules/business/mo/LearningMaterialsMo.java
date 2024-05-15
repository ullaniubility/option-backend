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
@TableName("sys_learning_materials")
@ApiModel(value = "learningMaterials学习资料对象", description = "")
public class LearningMaterialsMo implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

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
