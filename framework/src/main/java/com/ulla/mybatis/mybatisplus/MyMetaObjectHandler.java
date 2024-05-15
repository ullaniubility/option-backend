package com.ulla.mybatis.mybatisplus;

import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.ulla.common.utils.UserUtil;

/**
 * @Description 字段填充审计
 * @author zhuyongdong
 * @since 2022-12-30 21:24:33
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        // 有创建时间字段，字段值为空
        if (metaObject.hasGetter("createBy") && UserUtil.getUid() != null) {
            this.setFieldValByName("createBy", UserUtil.getUid(), metaObject);
        }
        // 有创建时间字段，字段值为空
        if (metaObject.hasGetter("createTime")) {
            this.setFieldValByName("createTime", System.currentTimeMillis(), metaObject);
        }
        // 有值，则写入
        if (metaObject.hasGetter("deleteFlag")) {
            this.setFieldValByName("deleteFlag", 0, metaObject);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // 有创建时间字段，字段值为空
        if (metaObject.hasGetter("updateBy") && UserUtil.getUid() != null) {
            this.setFieldValByName("updateBy", UserUtil.getUid(), metaObject);
        }
        // 更新时自动填充...
        if (metaObject.hasGetter("updateTime")) {
            this.setFieldValByName("updateTime", System.currentTimeMillis(), metaObject);
        }
    }

}
