package xifu.com.vo;

/**
 * 分页信息的请求参数
 * @auth wq on 2019/1/18 16:03
 **/
public class PageRequestInfo {
    private static final Integer DEFAULT_PAGE = 1; // 默认页
    private static final Integer DEFAULT_PAGE_SIZE = 10; // 默认的每页显示的记录数量
    private Integer page;
    private Integer pageSize;

    public Integer getPage() {
        if(page == null) {
            return DEFAULT_PAGE;
        }
        return Math.max(page, DEFAULT_PAGE); // 不能小于1页
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        if (pageSize == null) {
            return DEFAULT_PAGE_SIZE;
        }
        return Math.max(pageSize, DEFAULT_PAGE_SIZE); // 每页显示的数量最小不少于10条
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
