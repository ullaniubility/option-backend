package com.ulla.modules.business.mo;

import java.io.Serializable;

import org.springframework.data.annotation.CreatedDate;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
@TableName("biz_login_device")
@ApiModel(value = "LoginDevice登录设备管理", description = "")
@ToString
public class LoginDeviceMo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    @ApiModelProperty(value = "唯一标识", hidden = true)
    private Long id;

    @TableField("ip")
    private String ip;

    @TableField("area")
    private String area;

    @CreatedDate
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间", hidden = true)
    private Long createTime;

    private Integer loginPlatformType;

    private Long uid;

    private String token;
}
