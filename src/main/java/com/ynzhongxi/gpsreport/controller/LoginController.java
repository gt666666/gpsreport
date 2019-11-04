package com.ynzhongxi.gpsreport.controller;

import com.ynzhongxi.gpsreport.pojo.Member;
import com.ynzhongxi.gpsreport.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 类描述：
 * 创建作者：gt
 * 创建日期 ： 2019/10/10
 */
@RestController
public class LoginController {
    @Autowired
    private LoginService loginService;

    @GetMapping("/login")
    public int login(Member member, HttpServletRequest request) {
        return this.loginService.login(member,request);
    }
}
