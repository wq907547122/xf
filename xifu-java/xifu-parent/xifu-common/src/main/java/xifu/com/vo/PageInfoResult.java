package xifu.com.vo;

import java.util.List;

/**
 * 分页信息
 * @auth wq on 2019/1/3 13:37
 **/
public class PageInfoResult<T> {
    // 总记录数
    private Long total;
    // 总页数
    private Integer totalPage;
    // 分页的记录信息
    private List<T> list;

    public Long getTotal() {
        return total;
    }

    public PageInfoResult setTotal(Long total) {
        this.total = total;
        return this;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public PageInfoResult setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
        return this;
    }

    public List<T> getList() {
        return list;
    }

    public PageInfoResult setList(List<T> list) {
        this.list = list;
        return this;
    }
}
