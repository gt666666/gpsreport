package com.ynzhongxi.gpsreport.dao;

import com.ynzhongxi.gpsreport.pojo.Member;
import org.springframework.stereotype.Component;

/**
 * 类描述：
 * 创建作者：gt
 * 创建日期 ： 2019/10/10
 */
@Component
public class LoginDAO  extends BaseMongoDbDao<Member> {
    @Override
    protected Class<Member> getEntityClass() {
        return Member.class;
    }

}
