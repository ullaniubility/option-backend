package com.ulla.binance.mo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ulla.binance.mybatis.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @Description 交易类
 * @author zhuyongdong
 * @since 2023-02-27 22:50:31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qa_transaction_category_child")
@ToString
public class TransactionCategoryChildMo extends BaseEntity {

    /**
     * 交易类名称
     */
    @TableField("child_name")
    @NotBlank(message = "交易类名称不能为空")
    private String childName;
    /**
     * 一级交易类图标链接
     */
    @TableField("logo_url")
    private String logoUrl;
    /**
     * 一级交易类编号
     */
    @NotNull(message = "一级交易类编号不能为空")
    @TableField("category_id")
    private Long categoryId;
    /**
     * 一级交易类名称
     */
    @NotBlank(message = "一级交易类名称不能为空")
    @TableField("category_name")
    private String categoryName;
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
     * 盈利百分比（%)
     */
    @NotNull(message = "盈利百分比不能为空")
    @TableField("profit_percent")
    private Integer profitPercent;
    /**
     * 结算时间档位
     */
    @TableField("section_id")
    private Long sectionId;
    /**
     * 是否启用 0.启用 1.关闭
     */
    @TableField("using_flag")
    private Integer usingFlag;
    /**
     * 是否生效（技术手动改） 0.是 1.否
     */
    @TableField("status_flag")
    private Integer statusFlag;

    @TableField("is_popular")
    private Integer isPopular;

    @TableField("exercise_price")
    private Integer exercisePrice;

}
