package com.ynzhongxi.gpsreport.service;

import cn.hutool.core.collection.CollUtil;
import com.ynzhongxi.gpsreport.dao.TestDao;
import com.ynzhongxi.gpsreport.enums.ResultEnum;
import com.ynzhongxi.gpsreport.exception.FailException;
import com.ynzhongxi.gpsreport.pojo.Page;
import com.ynzhongxi.gpsreport.pojo.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 测试业务类
 *
 * @author lixingwu
 */
@Service
public class BaseService {
    private final TestDao dao;

    @Autowired
    public BaseService(TestDao testDao) {
        this.dao = testDao;
    }

    /**
     * 方法描述：保存记录.
     * 创建时间：2019-06-15 15:11:46
     * 创建作者：李兴武
     *
     * @param record 要保保存的记录
     * @author "lixingwu"
     */
    public void save(Test record) {
        dao.save(record);
    }

    /**
     * 方法描述：根据记录的 objectId 进行查询.
     * 创建时间：2019-06-15 16:23:26
     * 创建作者：李兴武
     *
     * @param id objectId的字符串
     * @return LogBusiness
     * @author "lixingwu"
     */
    public Test queryById(String id) {
        return dao.queryById(id);
    }

    /**
     * 方法描述：根据条件查询list.
     * 创建时间：2019-06-15 16:46:13
     * 创建作者：李兴武
     *
     * @param record 查询条件对象
     * @author "lixingwu"
     */
    public List<Test> queryList(Test record) {
        List<Test> list = dao.queryList(record);
        if (null == list) {
            return CollUtil.newArrayList();
        }
        return list;
    }

    /**
     * 方法描述：根据条件查询，如果根据条件查询出多条，只获取第一条记录.
     * 创建时间：2019-06-15 17:00:22
     * 创建作者：李兴武
     *
     * @param record 查询条件对象
     * @author "lixingwu"
     */
    public Test queryOne(Test record) {
        return dao.queryOne(record);
    }

    /**
     * 方法描述：查询分页数据.
     * 创建时间：2019-06-15 17:06:13
     * 创建作者：李兴武
     *
     * @param record   查询条件
     * @param page     查询的页数
     * @param pageSize 每页显示的条数
     * @return the list
     * @author "lixingwu"
     */
    public Page<Test> getPage(Test record, int page, int pageSize) {
        List<Test> pageList = dao.getPage(record, page, pageSize);
        Long count = dao.getCount(record);
        return new Page<>(page, pageSize, count, pageList);
    }

    /**
     * 方法描述：删除对象record.
     * 创建时间：2019-06-15 20:27:45
     * 创建作者：李兴武
     *
     * @param record 要删除的对象
     * @author "lixingwu"
     */
    public void delete(Test record) {
        if (dao.delete(record) < 1) {
            throw new FailException(ResultEnum.ERROR_DELETE);
        }
    }

    /**
     * 方法描述：根据id删除记录.
     * 创建时间：2019-06-15 20:47:20
     * 创建作者：李兴武
     *
     * @param id 记录id
     * @author "lixingwu"
     */
    public void deleteById(String id) {
        if (dao.deleteById(id) != 1) {
            throw new FailException(ResultEnum.ERROR_DELETE);
        }
    }

    /**
     * 方法描述：把匹配到srcObj的第一条记录修改为targetObj.
     * 创建时间：2019-06-15 20:52:12
     * 创建作者：李兴武
     *
     * @param record    查询条件
     * @param targetObj 修改为
     * @author "lixingwu"
     */
    public void updateFirst(String record, Test targetObj) {
        dao.updateFirst(record, targetObj);
    }

    /**
     * 方法描述：把匹配到srcObj的记录修改为targetObj.
     * 创建时间：2019-06-15 20:52:12
     * 创建作者：李兴武
     *
     * @param record    查询条件
     * @param targetObj 修改为
     * @author "lixingwu"
     */
    public void updateMulti(Test record, Test targetObj) {
        dao.updateMulti(record, targetObj);
    }
}
