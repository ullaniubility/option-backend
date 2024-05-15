package com.ulla.common.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 分页工具类
 *
 * @author michael
 */
@Data
public class PageUtils implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 总记录数
     */
    private int totalCount;
    /**
     * 每页记录数
     */
    private int pageSize;
    /**
     * 总页数
     */
    private int totalPage;
    /**
     * 当前页数
     */
    private int currPage;
    /**
     * 列表数据
     */
    private List<?> list;

    /**
     * 分页
     *
     * @param list       列表数据
     * @param totalCount 总记录数
     * @param pageSize   每页记录数
     * @param currPage   当前页数
     */
    public PageUtils(List<?> list, int totalCount, int pageSize, int currPage) {
        this.list = list;
        this.totalCount = totalCount;
        this.pageSize = pageSize;
        this.currPage = currPage;
        this.totalPage = (int) Math.ceil((double) totalCount / pageSize);
    }


    /**
     * 分页
     */
    public PageUtils(IPage<?> page) {
        this.list = page.getRecords();
        this.totalCount = (int) page.getTotal();
        this.pageSize = (int) page.getSize();
        this.currPage = (int) page.getCurrent();
        this.totalPage = (int) page.getPages();
    }

    public PageUtils(Integer pageSize, Integer currPage) {
        if (pageSize == null) {
            pageSize = 20;
        }
        if (currPage == null) {
            currPage = 1;
        }
        this.pageSize = pageSize;
        this.currPage = currPage;
    }

    public PageUtils(List<?> list, Integer pageNum, Integer pageSize){

        this.currPage = pageNum;
        this.pageSize = pageSize;
        this.totalCount = list.size();

        //总记录数和每页显示的记录之间是否可以凑成整数（pages）
        boolean full = totalCount % pageSize == 0;

        //分页 == 根据pageSize（每页显示的记录数）计算pages
        if(!full){
            //如果凑不成整数
            this.totalPage = totalCount/pageSize + 1;
        }else{
            //如果凑成整数
            this.totalPage = totalCount/pageSize;
        }

        int fromIndex = 0;
        int toIndex   = 0;
        fromIndex = pageNum*pageSize-pageSize;
        if(pageNum == 0){
            throw new ArithmeticException("第0页无法展示");
        }else if(pageNum>totalPage){
            //如果查询的页码数大于总的页码数，list设置为[]
            list = new ArrayList<>();
        }else if(pageNum == totalPage){
            //如果查询的当前页等于总页数，直接索引到total处
            toIndex = totalCount;
        }else{
            //如果查询的页码数小于总页数，不用担心切割List的时候toIndex索引会越界，直接等
            toIndex   = pageNum*pageSize;
        }

        if(list.size() == 0){
            this.list = list;
        }else{
            this.list = list.subList(fromIndex, toIndex);
        }

    }


    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getCurrPage() {
        return currPage;
    }

    public void setCurrPage(int currPage) {
        this.currPage = currPage;
    }

    public List<?> getList() {
        return list;
    }

    public void setList(List<?> list) {
        this.list = list;
    }

}
