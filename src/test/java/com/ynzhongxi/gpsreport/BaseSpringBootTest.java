package com.ynzhongxi.gpsreport;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Console;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;
import java.util.List;

/**
 * 测试基类
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public abstract class BaseSpringBootTest {

    @Resource
    private WebApplicationContext applicationContext;

    protected MockMvc mock;

    private long time;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Before
    public void setUp() {
        this.setTime(System.currentTimeMillis());
        mock = MockMvcBuilders.webAppContextSetup(applicationContext).build();
        print("==> 测试开始执行 <==");
    }

    @After
    public void tearDown() {
        print("==> 测试执行完成，耗时：{} <==", DateUtil.formatBetween(System.currentTimeMillis() - this.getTime()));
    }

    /**
     * 方法描述：打印list.
     */
    protected <T> void print(List<T> list) {
        if (!CollectionUtils.isEmpty(list)) {
            for (Object obj : list) {
                Console.log(obj);
            }
        }
    }

    /**
     * 方法描述：打印模板信息.
     */
    protected void print(String template, Object... values) {
        Console.log(template, values);
    }

    /**
     * 方法描述：打印对象信息.
     */
    protected void print(Object... values) {
        Console.log(values);
    }


    /**
     * 方法描述：（红色）打印list.
     */
    protected <T> void error(List<T> list) {
        if (!CollectionUtils.isEmpty(list)) {
            for (Object obj : list) {
                Console.error(obj);
            }
        }
    }

    /**
     * 方法描述：（红色）打印对象信息.
     */
    protected void error(Object... values) {
        Console.error(values);
    }

    /**
     * 方法描述：（红色）打印模板信息.
     */
    protected void error(String template, Object... values) {
        Console.error(template, values);
    }

    /**
     * 设置请求的根目录
     */
    public abstract String baseUrl();

    /**
     * 模拟发送一个post请求
     *
     * @param mock   MockMvc
     * @param url    相对路径
     * @param params 参数集合
     * @return CommonResult
     * @throws Exception
     */
    protected String post(MockMvc mock, String url, MultiValueMap<String, String> params) throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.post(baseUrl() + url).params(params);
        MvcResult result = mock.perform(request).andReturn();
        return result.getResponse().getContentAsString();
    }

    /**
     * 模拟发送一个get请求
     *
     * @param mock   MockMvc
     * @param url    相对路径
     * @param params 参数集合
     * @return CommonResult
     * @throws Exception
     */
    protected String get(MockMvc mock, String url, MultiValueMap<String, String> params) throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get(baseUrl() + url).params(params);
        MvcResult result = mock.perform(request).andReturn();
        return result.getResponse().getContentAsString();
    }

}
