package com.ulla.binance.mybatis;

import java.io.Serializable;

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
public abstract class BaseIdEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private Long id;

}
