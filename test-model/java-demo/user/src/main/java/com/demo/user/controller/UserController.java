package com.demo.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.demo.user.form.UserForm;
import com.demo.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class UserController {


    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public String login(@RequestBody UserForm userForm) {

        return userService.login(userForm);
    }

    @GetMapping("/detail")
    public JSONObject detail(@RequestParam("id") Long id) {

        return userService.detail(id);
    }
}
