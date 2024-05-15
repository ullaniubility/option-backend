package com.ulla.modules.business.mo;

import javax.validation.constraints.NotBlank;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ulla.mybatis.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @Description 交易一级大类
 * @author zhuyongdong
 * @since 2023-02-27 22:50:31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qa_transaction_category")
@ToString
public class TransactionCategoryMo extends BaseEntity {

    /**
     * 一级交易类名称
     */
    @NotBlank(message = "一级交易类名称不能为空")
    @TableField("category_name")
    private String categoryName;

    /**
     * 一级交易类图标链接
     */
    @TableField("logo_url")
    private String logoUrl;

    /**
     * 排序规则 1.首字母缩写正序 2.首字母缩写倒序 3.编号排序正序 4.编号排序倒序
     */
    @TableField("sort_rule")
    private Integer sortRule;

    /**
     * 排序编号
     */
    @TableField("sort_number")
    private Integer sortNumber;

    /**
     * 是否启用 0.启用 1.关闭
     */
    @TableField("using_flag")
    private Integer usingFlag;

}
