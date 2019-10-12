package com.ynzhongxi.gpsreport.config;

import cn.hutool.core.lang.Dict;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.json.JSONUtil;
import com.ynzhongxi.gpsreport.enums.ResultEnum;
import com.ynzhongxi.gpsreport.utils.Tools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Session 过期拦截器
 *
 * @author lixingwu
 */
@Component
@Slf4j
public class SessionInterceptors implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        HttpSession session = request.getSession(true);
        if (session.getAttribute("mid") != null) {  //有登录过
                  return true;
        }
        else {
            return false;
        }

    }
}
