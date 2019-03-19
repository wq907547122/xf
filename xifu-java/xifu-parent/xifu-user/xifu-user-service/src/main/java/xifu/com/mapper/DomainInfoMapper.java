package xifu.com.mapper;

import tk.mybatis.mapper.common.Mapper;
import xifu.com.pojo.DomainInfo;

import java.util.List;

/**
 * 区域表对应的实体类的mapper
 * @auth wq on 2019/1/22 14:16
 **/
public interface DomainInfoMapper extends Mapper<DomainInfo> {
    /**
     * 获取企业下的所有区域，包括子区域，查询的时候使用了mybaties的collection结果来递归查询
     * @param enterpriseId
     * @return
     */
    List<DomainInfo> getNodeTree(Long enterpriseId);
}
