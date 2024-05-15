package com.ulla.binance.mybatis;

import java.io.Serializable;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 数据库基础实体类
 *
 * @author Chopper
 * @version v1.0
 * @since 2020/8/20 14:34
 */

@Data
@JsonIgnoreProperties(value = {"handler", "fieldHandler"})
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private Long id;

    @CreatedBy
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    @CreatedDate
    @TableField(fill = FieldFill.INSERT)
    private Long createTime;

    @LastModifiedBy
    @TableField(fill = FieldFill.UPDATE)
    private Long updateBy;

    @LastModifiedDate
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateTime;

    @TableField(fill = FieldFill.INSERT)
    private Integer isDelete;

}
