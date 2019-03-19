package xifu.com.service;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ScrolledPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import xifu.com.es.InverterRepository;
import xifu.com.exception.ExceptionEnum;
import xifu.com.exception.XiFuException;
import xifu.com.pojo.Inverter;
import xifu.com.vo.PageInfoResult;
import xifu.com.vo.SearchPageResult;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @auth wq on 2019/1/7 19:18
 **/
@Service
public class DbService {
    @Autowired
    private InverterRepository inverterRepository;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    /**
     * 查询所有
     * @return
     */
    public List<Inverter> queryAll() {
        Iterable<Inverter> all = inverterRepository.findAll();
        Iterator<Inverter> iterator = all.iterator();
        List<Inverter> list = new ArrayList<>();
        while (iterator.hasNext()){
            list.add(iterator.next());
        }
        if(CollectionUtils.isEmpty(list)){
            throw new XiFuException(ExceptionEnum.INVERTER_NOT_FOUND);
        }
        return list;
    }

    /**
     * 查询分页信息，这个限制只能搜索前面默认：10000条数据，如果要修改，需要修改elasticsearch的配置，这样消耗更加内存
     * @param page
     * @param pageSize
     * @return
     */
    public PageInfoResult<Inverter> queryByPage(String key, int page, int pageSize){
        Pageable pageable = PageRequest.of(page - 1, pageSize); // es分页的查询是从0开始的
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if(StringUtils.isNotBlank(key)) {
            queryBuilder.must(QueryBuilders.termQuery("devName", key));
        }
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                // 分页
                .withPageable(pageable)
                // 排序
                .withSort(SortBuilders.fieldSort("collectTime").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("devId").order(SortOrder.ASC))
                .build();
        Page<Inverter> search = inverterRepository.search(searchQuery);
        if(CollectionUtils.isEmpty(search.getContent())){
            throw new XiFuException(ExceptionEnum.INVERTER_NOT_FOUND);
        }
        PageInfoResult<Inverter> result = new PageInfoResult<>();
        result.setTotal(search.getTotalElements());
        result.setList(search.getContent());
        return result;
    }

    /**
     * 查询所有的Query
     * @return
     */
    private QueryBuilder matchAllQuery() {
        return QueryBuilders.matchAllQuery();
    }
    /**
     * 使用滚动搜索,可以搜索的内容
     * @param key
     * @param page
     * @param pageSize
     * @param scrollId: 滚动id
     * @return
     */
    public PageInfoResult<Inverter> queryByPageScorll(String key, int page, int pageSize, String scrollId){
        BoolQueryBuilder filter = QueryBuilders.boolQuery();
        if(StringUtils.isNotBlank(key)) { // 过滤
            filter.must(QueryBuilders.termQuery("devName", key));
        }
        filter.must(QueryBuilders.rangeQuery("devId").gte(10000100));
        // 使用滚动搜索
        if(StringUtils.isBlank(scrollId)) { // 如果没有scrollId
            SearchQuery searchQuery = new NativeSearchQueryBuilder()
                    .withQuery(matchAllQuery()) // 使用过滤器可以提高查询速度+
                    .withIndices("inverter_index")
                    .withTypes("inverter")
                    .withFilter(filter)
                    .withPageable(PageRequest.of(0, pageSize))
                    .withSort(SortBuilders.fieldSort("devId").order(SortOrder.ASC))
                    .build();
            ScrolledPage<Inverter> scrolledPage = (ScrolledPage<Inverter>)elasticsearchTemplate.
                    startScroll(100000L, searchQuery, Inverter.class);
            String scrollId1 = scrolledPage.getScrollId();
            List<Inverter> content = scrolledPage.getContent();
            long totalElements = scrolledPage.getTotalElements();
            SearchPageResult<Inverter> result = new SearchPageResult<>();
            result.setScrollId(scrollId1);
            result.setList(content);
            result.setTotal(totalElements);
            return result;
        } else { // 如果有scrollId
            ScrolledPage<Inverter> scrolledPage = (ScrolledPage<Inverter>)elasticsearchTemplate.continueScroll(scrollId, 100000L, Inverter.class);
            SearchPageResult<Inverter> result = new SearchPageResult<>();
            result.setScrollId(scrolledPage.getScrollId());
            result.setTotal(scrolledPage.getTotalElements());
            result.setList(scrolledPage.getContent());
            return result;
        }

//        Pageable pageable = PageRequest.of(page - 1, pageSize);
//        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
//        if(StringUtils.isNotBlank(key)) {
//            queryBuilder.must(QueryBuilders.termQuery("devName", key));
//        }
//        SearchQuery searchQuery = new NativeSearchQueryBuilder()
//                .withQuery(queryBuilder)
//                // 分页
//                .withPageable(pageable)
//                // 排序
//                .withSort(SortBuilders.fieldSort("collectTime").order(SortOrder.DESC))
//                .withSort(SortBuilders.fieldSort("devId").order(SortOrder.ASC))
//                .build();
//        Page<Inverter> search = inverterRepository
//        if(CollectionUtils.isEmpty(search.getContent())){
//            throw new XiFuException(ExceptionEnum.INVERTER_NOT_FOUND);
//        }
//        PageInfoResult<Inverter> result = new PageInfoResult<>();
//        result.setTotal(search.getTotalElements());
//        result.setList(search.getContent());
//        return result;
    }
}
