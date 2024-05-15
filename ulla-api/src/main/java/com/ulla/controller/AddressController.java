package com.ulla.controller;

import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import com.ulla.common.controller.UserBaseController;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.ulla.common.vo.ResultMessageVo;
import com.ulla.modules.assets.service.AddressService;
import com.ulla.modules.assets.vo.AddressVo;

import lombok.RequiredArgsConstructor;

/**
 * <p>
 * 用户钱包地址表 前端控制器
 * </p>
 *
 * @author jetBrains
 * @since 2023-02-28
 */
@RestController
@RequestMapping("/address")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Validated
public class AddressController extends UserBaseController {

    final AddressService addressService;

    @PostMapping(value = "/importAddress")
    public ResultMessageVo importAddress(@RequestBody List<AddressVo> addressVos) {
        return addressService.importAddress(addressVos);
    }

    @GetMapping(value = "/selectAddress")
    public ResultMessageVo selectAddress(@RequestParam("isAll") @Min(0) @Max(1) Integer isAll) {
        return addressService.selectAddress(isAll);
    }

    @GetMapping(value = "/bindAddress")
    public ResultMessageVo bindAddress(@RequestParam("type") @Min(1) @Max(2) Integer type,
        @RequestParam("uid") @Length(min = 1) String uid) {
        return addressService.bindAddress(type, getUserId(uid));
    }
}
