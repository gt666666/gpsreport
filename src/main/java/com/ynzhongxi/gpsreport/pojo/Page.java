package com.ynzhongxi.gpsreport.pojo;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.PageUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lixingwu
 */
@Data
public class Page<T> implements Serializable {
    private static final long serialVersionUID = 8656597559014685635L;
    public static final int DEFAULT_PAGE_SIZE = 20;
    private List<T> data;
    private int page;
    private int pageSize;
    private int totalPage;
    private Long total;
    private int code;

    /**
     * 初始化一个分页对象那个.
     *
     * @param page     当前也页码
     * @param pageSize 每页记录数
     * @param total    总记录数目
     * @param data     当前分页数据
     */
    public Page(int page, int pageSize, long total, List<T> data) {
        this.total = (total <= 0 ? 0 : total);
        this.page = (page <= 0 ? 0 : page);
        this.pageSize = (pageSize <= 0 ? DEFAULT_PAGE_SIZE : pageSize);
        this.totalPage = PageUtil.totalPage(Convert.toInt(this.total), this.pageSize);
        this.code = (data == null || data.isEmpty() ? 0 : 200);
        this.data = (data == null ? new ArrayList<>() : data);
    }
}
