package com.ynzhongxi.gpsreport.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 防止攻击filter
 *
 * @author lixingwu
 */
@Order(3)
@WebFilter(filterName = "xssCodeFilter", urlPatterns = "/*")
public class XssCodeFilter implements Filter {
    FilterConfig filterConfig = null;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void init(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        logger.debug(">>> 请求路径：{}", request.getRequestURL());
        filterChain.doFilter(new XssCodeWrapper((HttpServletRequest) servletRequest), servletResponse);
    }

    @Override
    public void destroy() {
        this.filterConfig = null;
    }
}
