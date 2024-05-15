package com.ulla.binance.mybatis.mybatisplus;

import java.util.Date;

import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;

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
        if (metaObject.hasGetter("createTime")) {
            this.setFieldValByName("createTime", new Date(), metaObject);
        }
        // 有值，则写入
        if (metaObject.hasGetter("deleteFlag")) {
            this.setFieldValByName("deleteFlag", false, metaObject);
        }

    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // 更新时自动填充...
        if (metaObject.hasGetter("updateTime")) {
            this.setFieldValByName("updateTime", new Date(), metaObject);
        }
    }

}
