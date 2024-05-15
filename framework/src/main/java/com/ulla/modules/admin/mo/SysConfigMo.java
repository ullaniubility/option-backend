package com.ulla.modules.admin.mo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ulla.mybatis.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_config")
public class SysConfigMo extends BaseEntity {

    /**
     * 参数名
     */
    @TableField("config_name")
    private String configName;

    /**
     * 键
     */
    @TableField("config_key")
    private String configKey;

    /**
     * 值
     */
    @TableField("config_value")
    private String configValue;

    /**
     * 配置类型
     */
    @TableField("config_type")
    private String configType;

    /**
     * 备注0
     */
    @TableField("remark")
    private String remark;
}
