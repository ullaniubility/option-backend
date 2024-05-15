package com.ulla.modules.auth.qo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @Description {用户qo}
 * @author {clj}
 * @since {2023-2-8}
 */
@ApiModel(value = "用户Qo")
@Data
@ToString
public class UserLoginInfoQo {

    private static final long serialVersionUID = 1L;

    /**
     * ip
     */
    @ApiModelProperty(value = "ip")
    private String ip;

    /**
     * ip解析出的地址
     */
    @ApiModelProperty(value = "area")
    private String area;

    /**
     * 是否游客用户 1.是 0. 否
     */
    @ApiModelProperty(value = "ifGuest")
    private Integer ifGuest;

    /**
     * 登录平台
     */
    @ApiModelProperty(value = "platform")
    private String platform;

    /**
     * 设备指纹
     */
    @ApiModelProperty(value = "fingerprint")
    private String fingerprint;

    /**
     * 登录时间
     */
    @ApiModelProperty(value = "create_time")
    private Long createTime;

    private Integer deleteFlag;

}
