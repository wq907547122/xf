package xifu.com.vo;

import lombok.Data;

/**
 * @auth wq on 2019/1/14 16:25
 **/
@Data
public class SearchPageResult<T> extends PageInfoResult<T> {
    private String scrollId; // 滚动的id
}
