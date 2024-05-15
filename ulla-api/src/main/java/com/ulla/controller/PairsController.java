package com.ulla.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ulla.cache.Cache;
import com.ulla.common.utils.ResultUtil;
import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.business.mo.PairsMo;
import com.ulla.modules.business.mo.TransactionCategoryChildMo;
import com.ulla.modules.business.service.IPairsService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@Api(tags = "交易对列表")
@RestController
@RequestMapping("/pairs")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PairsController {
    final IPairsService pairsService;

    final Cache cache;

    /**
     * 客户端无分页
     */
    @ApiOperation(value = "交易对列表列表", notes = "交易对列表列表")
    @GetMapping("/list")
    public ResultMessageVo PairsList() {
        List<TransactionCategoryChildMo> listChild =
            (List<TransactionCategoryChildMo>)cache.get("binance:api:symbolList");
        ArrayList<PairsMo> pairsMos = new ArrayList<PairsMo>();
        for (TransactionCategoryChildMo mo : listChild) {
            PairsMo pairsMo = new PairsMo();
            pairsMo.setName(mo.getChildName());
            pairsMos.add(pairsMo);
        }
        return ResultUtil.data(pairsMos);
    }
}
