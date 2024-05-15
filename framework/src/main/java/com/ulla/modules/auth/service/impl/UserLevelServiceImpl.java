package com.ulla.modules.auth.service.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.ulla.common.utils.ResultUtil;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.auth.mapper.UserLevelMapper;
import com.ulla.modules.auth.mo.UserLevelMo;
import com.ulla.modules.auth.qo.UserLevelQo;
import com.ulla.modules.auth.service.UserLevelService;

import cn.hutool.json.JSONObject;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserLevelServiceImpl extends ServiceImpl<UserLevelMapper, UserLevelMo> implements UserLevelService {

    final UserLevelMapper userLevelMapper;

    @Override
    public ResultMessageVo getList() {
        LambdaQueryWrapper<UserLevelMo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserLevelMo::getOffFlag, 0);
        wrapper.orderByAsc(UserLevelMo::getId);
        List<UserLevelMo> list = userLevelMapper.selectList(wrapper);
        List<List<String>> listStr = convert(list);
        List<JSONObject> resultList = Lists.newArrayList();
        for (int i = 0; i < listStr.size(); i++) {
            JSONObject object = new JSONObject();
            for (int j = 0; j < listStr.get(i).size(); j++) {
                object.set("key" + j, listStr.get(i).get(j));
            }
            resultList.add(object);
        }
        return ResultUtil.data(resultList);
    }

    private static List<List<String>> convert(List<UserLevelMo> list) {
        try {
            Field[] declaredFields = UserLevelMo.class.getDeclaredFields();
            List<List<String>> convertedTable = Lists.newArrayList();
            // 多少个属性表示多少行，遍历行
            int p = 0;
            for (Field field : declaredFields) {
                if (field.getName().equals("sortNum") || field.getName().equals("offFlag")
                    || field.getName().equals("serialVersionUID")) {
                    continue;
                }
                field.setAccessible(true);
                ArrayList<String> rowLine = new ArrayList<String>();
                if (p == 0) {
                    rowLine.add("等级");
                } else if (p == 1) {
                    rowLine.add("入金金额（$），升级条件");
                } else if (p == 2) {
                    rowLine.add("允许同时进行的最大交易数量（笔数）");
                } else if (p == 3) {
                    rowLine.add("单笔最大交易金额（$）");
                } else if (p == 4) {
                    rowLine.add("增加资产收益百分比（%）");
                }
                for (int i = 0, size = list.size(); i < size; i++) {
                    UserLevelMo userLevelMo = list.get(i);
                    String val = String.valueOf(field.get(userLevelMo));
                    rowLine.add(val);
                }
                convertedTable.add(rowLine);
                p++;
            }
            return convertedTable;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ResultMessageVo updateUserLevel(UserLevelQo userLevelQo) {
        UserLevelMo userLevelMo = new UserLevelMo();
        BeanUtils.copyProperties(userLevelQo, userLevelMo);
        userLevelMapper.updateById(userLevelMo);
        return ResultUtil.success();
    }

    @Override
    public ResultMessageVo addUserLevel(UserLevelQo userLevelQo) {
        UserLevelMo userLevelMo = new UserLevelMo();
        BeanUtils.copyProperties(userLevelQo, userLevelMo);
        userLevelMapper.insert(userLevelMo);
        return ResultUtil.success();
    }

}
