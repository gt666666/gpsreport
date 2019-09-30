package com.ynzhongxi.gpsreport.dao;

import cn.hutool.core.util.PageUtil;
import com.ynzhongxi.gpsreport.pojo.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * @author lixingwu
 */
@Slf4j
public abstract class BaseMongoDbDao<T> {

    /**
     * 反射获取泛型类型
     */
    protected abstract Class<T> getEntityClass();

    @Resource
    private MongoTemplate mongoTemplate;

    /***
     * 保存一个对象t
     */
    public void save(T t) {
        log.info(">>> MongoDB save start，{}", t);
        this.mongoTemplate.save(t);
    }

    /***
     * 根据id从几何中查询对象
     */
    public T queryById(String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        log.info(">>> MongoDB find start，{}", query.toString());
        return this.mongoTemplate.findOne(query, this.getEntityClass());
    }

    /**
     * 根据条件object查询集合
     */
    public List<T> queryList(T object) {
        Query query = getQueryByObject(object);
        log.info(">>> MongoDB find start，{}", query.toString());
        return this.mongoTemplate.find(query, this.getEntityClass());
    }

    /**
     * 根据条件object查询只返回一个文档
     */
    public T queryOne(T object) {
        Query query = getQueryByObject(object);
        log.info(">>> MongoDB find start，{}", query.toString());
        return mongoTemplate.findOne(query, this.getEntityClass());
    }

    /***
     * 根据条件object分页查询
     * @param page 查询的页数
     * @param size  查询大小
     */
    public List<T> getPage(T object, int page, int size) {
        int[] startEnd = PageUtil.transToStartEnd(page, size);
        Query query = getQueryByObject(object);
        query.skip(startEnd[0]);
        query.limit(size);
        log.info(">>> MongoDB queryPage start，{}", query.toString());
        return this.mongoTemplate.find(query, this.getEntityClass());
    }

    /***
     * 根据条件object查询库中符合条件的记录数量
     */
    public Long getCount(T object) {
        Query query = getQueryByObject(object);
        log.info(">>> MongoDB Count start，{}", query.toString());
        return this.mongoTemplate.count(query, this.getEntityClass());
    }

    /***
     * 根据条件object查询库中符合条件的记录数量
     */
    public Long getLikeCount(T object) {
        Query query = getQueryLikeByObject(object);
        log.info(">>> MongoDB Count start，{}", query.toString());
        return this.mongoTemplate.count(query, this.getEntityClass());
    }

    /***
     * 删除对象t
     */
    public int delete(T t) {
        Query query = this.getQueryByObject(t);
        log.info(">>> MongoDB delete start，{}", query.toString());
        int i = (int) this.mongoTemplate.remove(query, this.getEntityClass()).getDeletedCount();
        return   i;
    }

    /**
     * 根据id删除
     */
    public int deleteById(String id) {
        Criteria criteria = Criteria.where("_id").is(id);
        Query query = new Query(criteria);
        T obj = this.mongoTemplate.findOne(query, this.getEntityClass());
        log.info(">>> MongoDB deleteById start，{}", query.toString());
        if (obj != null) {
            return(int )this.mongoTemplate.remove(query,this.getEntityClass()).getDeletedCount();
        }
        return 0;
    }

    /**
     * 修改匹配到srcObj的第一条记录为targetObj
     */
    public void updateFirst(String  carNumber ,T targetObj) {
        Query  query=new Query(Criteria.where("carNumber").is(carNumber));
        Update update = getUpdateByObject(targetObj);
        log.info(">>> MongoDB updateFirst start，{}", query.toString());
        this.mongoTemplate.updateFirst(query, update, this.getEntityClass());
    }

    /***
     * 修改匹配到srcObj的所有记录为targetObj
     */
    public void updateMulti(T srcObj, T targetObj) {
        Query query = getQueryByObject(srcObj);
        Update update = getUpdateByObject(targetObj);
        log.info(">>> MongoDB updateFirst start，{}", query.toString());
        this.mongoTemplate.updateMulti(query, update, this.getEntityClass());
    }

    /***
     * 修改匹配到的记录srcObj，若不存在该记录targetObj则进行添加
     */
    public void updateInsert(T srcObj, T targetObj) {
        Query query = getQueryByObject(srcObj);
        Update update = getUpdateByObject(targetObj);
        log.info(">>> MongoDB updateInsert start，{}", query.toString());
        this.mongoTemplate.upsert(query, update, this.getEntityClass());
    }

    /**
     * 将查询object条件对象转换为query
     */
    public Query getQueryByObject(T object) {
        Query query = new Query();
        String[] fileds = getFiledName(object);
        Criteria criteria = new Criteria();
        Arrays.stream(fileds).forEach(filedName -> {
            Object filedValue = getFieldValueByName(filedName, object);
            if (filedValue != null) {
                criteria.and(filedName).is(filedValue);
            }
        });
        query.addCriteria(criteria);
        return query;
    }

    /**
     * 将查询object条件对象转换为query及逆行模糊查询
     **/
    public Query getQueryLikeByObject(T object) {
        Query query = new Query();
        String[] fileds = getFiledName(object);
        Criteria criteria = new Criteria();
        Arrays.stream(fileds).forEach(filedName -> {
            Object filedValue = getFieldValueByName(filedName, object);
            if (filedValue != null) {
                criteria.and(filedName).regex(".*?" + filedValue + ".*");
            }
        });
        query.addCriteria(criteria);
        return query;
    }

    /**
     * 将查询条件object对象转换为update
     */
    private Update getUpdateByObject(T object) {
        Update update = new Update();
        String[] fileds = getFiledName(object);
        for (String filed : fileds) {
            Object filedValue = getFieldValueByName(filed, object);
            if (filedValue != null) {
                update.set(filed, filedValue);
            }
        }
        return update;
    }

    /***
     * 获取对象o属性返回字符串数组
     */
    private static String[] getFiledName(Object o) {
        Field[] fields = o.getClass().getDeclaredFields();
        String[] fieldNames = new String[fields.length];

        for (int i = 0; i < fields.length; ++i) {
            fieldNames[i] = fields[i].getName();
        }

        return fieldNames;
    }

    /***
     * 根据属性fieldName获取对象o属性值
     */
    private static Object getFieldValueByName(String fieldName, Object o) {
        try {
            String e = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + e + fieldName.substring(1);
            Method method = o.getClass().getMethod(getter);
            return method.invoke(o);
        } catch (Exception var6) {
            return null;
        }
    }
}
