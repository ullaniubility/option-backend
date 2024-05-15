package com.ulla.modules.business.qo;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.ulla.common.vo.PageVo;

import cn.hutool.core.text.CharSequenceUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description 交易一级大类查询参数
 * @author zhuyongdong
 * @since 2023-02-27 22:50:31
 */
@Data
public class TransactionCategoryQo extends PageVo {

    /**
     * 一级交易类编号
     */
    @ApiModelProperty("一级交易类编号")
    private Long id;

    /**
     * 一级交易类名称
     */
    @ApiModelProperty("一级交易类名称")
    private String categoryName;

    /**
     * 是否启用 0.启用 1.关闭
     */
    @ApiModelProperty("是否启用 0.启用 1.关闭")
    private Integer usingFlag;

    /**
     * 排序规则 0.默认排序 1.首字母缩写正序 2.首字母缩写倒序 3.编号排序正序 4.编号排序倒序
     */
    @ApiModelProperty("排序规则 1.首字母缩写正序 2.首字母缩写倒序 3.编号排序正序 4.编号排序倒序")
    private Integer sortRule;

    public <T> QueryWrapper<T> queryWrapper() {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        if (ObjectUtils.isNotEmpty(id)) {
            queryWrapper.eq("id", id);
        }
        if (CharSequenceUtil.isNotEmpty(categoryName)) {
            queryWrapper.like("category_name", categoryName);
        }
        if (usingFlag != null) {
            queryWrapper.eq("using_flag", usingFlag);
        }
        if (sortRule != null) {
            switch (sortRule) {
                case 1:
                    queryWrapper.orderByAsc("category_name");
                    break;
                case 2:
                    queryWrapper.orderByDesc("category_name");
                    break;
                case 3:
                    queryWrapper.orderByAsc("sort_number");
                    break;
                case 4:
                    queryWrapper.orderByDesc("sort_number");
                    break;
            }
        }
        queryWrapper.eq("delete_flag", 0);
        return queryWrapper;
    }

}
