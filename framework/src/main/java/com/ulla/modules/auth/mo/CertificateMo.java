package com.ulla.modules.auth.mo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ulla.mybatis.BaseIdEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 证件类型
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_certificate")
public class CertificateMo extends BaseIdEntity {

    /**
     * 证件名称
     */
    @TableField("name")
    private String name;

    /**
     * 证件名称（英文简称）
     */
    @TableField("name_en")
    private String nameEn;

    /**
     * 证件号码验证规则
     */
    @TableField("validation_rule")
    private Integer validationRule;

    /**
     * 状态
     */
    @TableField("status")
    private Integer status;

}
