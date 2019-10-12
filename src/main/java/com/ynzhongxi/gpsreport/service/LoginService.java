package com.ynzhongxi.gpsreport.service;

import com.ynzhongxi.gpsreport.dao.LoginDAO;
import com.ynzhongxi.gpsreport.pojo.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

/**
 * 类描述：
 * 创建作者：gt
 * 创建日期 ： 2019/10/10
 */
@Service
public class LoginService {
    @Autowired
    private LoginDAO loginDAO;

    public int login(Member member , HttpServletRequest request) {
        Member mem = this.loginDAO.queryOne(member);
        request.getSession().setAttribute("mid",mem.getUserName());
        System.out.println(mem+"*****");
        if (mem != null) {
            return 1;
        }
        return 0;
    }
}
